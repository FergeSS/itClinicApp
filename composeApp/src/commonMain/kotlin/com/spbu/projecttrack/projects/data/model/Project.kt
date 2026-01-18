package com.spbu.projecttrack.projects.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsResponse(
    val projects: List<Project> = emptyList(),
    val tags: List<Tag> = emptyList()
)

@Serializable
data class ProjectDetailResponse(
    val project: ProjectDetail? = null,  // Одиночный проект, не массив!
    val tags: List<Tag> = emptyList(),
    val teams: List<Team>? = null,
    val members: List<Member>? = null,
    val users: List<User>? = null
)

@Serializable
data class FindManyRequest(
    val filters: Map<String, String> = emptyMap(),
    val page: Int = 1
)

@Serializable
data class Project(
    val id: String,
    val name: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val dateStart: String? = null,
    val dateEnd: String? = null,
    val slug: String? = null,
    val tags: List<String>? = null
)

@Serializable
data class ProjectDetail(
    val id: String,
    val name: String,
    val description: String? = null,
    val shortDescription: String? = null,
    val dateStart: String? = null,
    val dateEnd: String? = null,
    val slug: String? = null,
    val tags: List<String>? = null,
    val team: String? = null,
    val status: String? = null
)

@Serializable
data class Tag(
    val id: String,
    val name: String,
    val description: String? = null
)

@Serializable
data class Team(
    val id: String,
    val name: String,
    val description: String? = null
)

@Serializable
data class Member(
    val id: String,
    val name: String,
    val role: String? = null
)

@Serializable
data class User(
    val id: String,
    val username: String? = null,
    val email: String? = null
)

