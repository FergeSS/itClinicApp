package com.spbu.projecttrack.projects.data.api

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
    
    suspend fun getProjects(page: Int = 1): Result<ProjectsResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_FINDMANY
            val url = "$baseUrl$endpoint"
            println("📡 Запрос к API: POST $url")
            println("📦 Тело запроса: page=$page")
            
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(FindManyRequest(filters = emptyMap(), page = page))
            }
            
            val bodyText = response.bodyAsText()
            println("✅ Статус ответа: ${response.status}")
            println("📄 Тело ответа: ${bodyText.take(500)}...") // Первые 500 символов
            
            // Пытаемся распарсить
            val parsedResponse = response.body<ProjectsResponse>()
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
                    contentType(ContentType.Application.Json)
                    setBody(FindManyRequest(filters = emptyMap(), page = page))
                }
                val pageData = response.body<ProjectsResponse>()
                
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
            val response = client.get("$baseUrl$endpoint")
            Result.success(response.body<ProjectsResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNewProjects(): Result<ProjectsResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_NEW
            val response = client.get("$baseUrl$endpoint")
            Result.success(response.body<ProjectsResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProjectById(id: String): Result<ProjectDetailResponse> {
        return try {
            val endpoint = ApiConfig.Public.PROJECT_DETAIL.replace("{slug}", id)
            val response = client.get("$baseUrl$endpoint")
            Result.success(response.body<ProjectDetailResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

