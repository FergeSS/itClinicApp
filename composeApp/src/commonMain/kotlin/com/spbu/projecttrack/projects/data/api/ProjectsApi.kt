package com.spbu.projecttrack.projects.data.api
import kotlinx.serialization.json.Json

import com.spbu.projecttrack.core.network.ApiConfig
import com.spbu.projecttrack.projects.data.model.ProjectsResponse
import com.spbu.projecttrack.projects.data.model.ProjectDetailResponse
import com.spbu.projecttrack.projects.data.model.FindManyRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ProjectsApi(private val client: HttpClient) {

    private val baseUrl = ApiConfig.baseUrl
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun buildHttpError(status: HttpStatusCode): Exception {
        return IllegalStateException("HTTP ${status.value} ${status.description}")
    }

    private fun logResponse(response: HttpResponse, bodyText: String, maxChars: Int = 500) {
        println("✅ Статус ответа: ${response.status}")
        println("📄 Тело ответа: ${bodyText.take(maxChars)}...")
    }

    suspend fun getProjects(page: Int = 1): Result<ProjectsResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_FINDMANY
            val url = "$baseUrl$endpoint"
            println("📡 Запрос к API: POST $url")
            println("📦 Тело запроса: page=$page")

            val response = client.post(url) {
                accept(ContentType.Application.Json)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(FindManyRequest(filters = emptyMap(), page = page))
            }

            val bodyText = response.bodyAsText()
            logResponse(response, bodyText)
            if (!response.status.isSuccess()) {
                return Result.failure(buildHttpError(response.status))
            }

            val parsedResponse = json.decodeFromString(ProjectsResponse.serializer(), bodyText)
            println("✅ Успешно распарсено: ${parsedResponse.projects.size} проектов, ${parsedResponse.tags.size} тегов")

            Result.success(parsedResponse)
        } catch (e: Exception) {
            println("❌ Ошибка при запросе: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getAllProjects(): Result<ProjectsResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_FINDMANY
            // Загружаем несколько страниц, так как каждая страница возвращает только 5 проектов
            val allProjects = mutableListOf<com.spbu.projecttrack.projects.data.model.Project>()
            val allTags = mutableSetOf<com.spbu.projecttrack.projects.data.model.Tag>()

            // Загружаем первые 4 страницы (до 20 проектов)
            for (page in 1..4) {
                val response = client.post("$baseUrl$endpoint") {
                    accept(ContentType.Application.Json)
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    setBody(FindManyRequest(filters = emptyMap(), page = page))
                }
                val bodyText = response.bodyAsText()
                if (!response.status.isSuccess()) {
                    return Result.failure(buildHttpError(response.status))
                }
                val pageData = json.decodeFromString(ProjectsResponse.serializer(), bodyText)

                allProjects.addAll(pageData.projects)
                allTags.addAll(pageData.tags)

                // Если проектов меньше 5, значит это последняя страница
                if (pageData.projects.size < 5) break
            }

            Result.success(ProjectsResponse(
                projects = allProjects,
                tags = allTags.toList()
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveProjects(): Result<ProjectsResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_ACTIVE
            val response = client.get("$baseUrl$endpoint") {
                accept(ContentType.Application.Json)
            }
            val bodyText = response.bodyAsText()
            if (!response.status.isSuccess()) {
                return Result.failure(buildHttpError(response.status))
            }
            Result.success(json.decodeFromString(ProjectsResponse.serializer(), bodyText))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNewProjects(): Result<ProjectsResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_NEW
            val response = client.get("$baseUrl$endpoint") {
                accept(ContentType.Application.Json)
            }
            val bodyText = response.bodyAsText()
            if (!response.status.isSuccess()) {
                return Result.failure(buildHttpError(response.status))
            }
            Result.success(json.decodeFromString(ProjectsResponse.serializer(), bodyText))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProjectById(id: String): Result<ProjectDetailResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_DETAIL.replace("{slug}", id)
            val url = "$baseUrl$endpoint"

            println("📡 Запрос к API: GET $url")
            println("📦 Параметр (id/slug): $id")

            val response = client.get(url) {
                accept(ContentType.Application.Json)
            }

            val bodyText = response.bodyAsText()
            println("✅ Статус ответа: ${response.status}")
            println("📄 Тело ответа: ${bodyText.take(2000)}...") // первые 2000 символов
            if (!response.status.isSuccess()) {
                return Result.failure(buildHttpError(response.status))
            }

            // Парсим из текста (так мы гарантированно парсим именно то, что залогировали)
            val parsed = json.decodeFromString(ProjectDetailResponse.serializer(), bodyText)

            // Короткий дамп полей проекта (чтобы сразу видеть, что пришло)
            val p = parsed.project
            if (p == null) {
                println("⚠️ parsed.project == null")
            } else {
                println("🧩 ProjectDetail parsed dump:")
                println("  id=${p.id}")
                println("  name=${p.name}")
                println("  description=${p.description}")
                println("  shortDescription=${p.shortDescription}")
                println("  dateStart=${p.dateStart}")
                println("  dateEnd=${p.dateEnd}")
                println("  slug=${p.slug}")
                println("  tags=${p.tags}")
                println("  team=${p.team}")
                println("  status=${p.status}")
                println("  client=${p.client}")
                println("  contact=${p.contact}")
                println("  requirements=${p.requirements}")
                println("  executorRequirements=${p.executorRequirements}")
            }

            Result.success(parsed)
        } catch (e: Exception) {
            println("❌ Ошибка при запросе getProjectById: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
