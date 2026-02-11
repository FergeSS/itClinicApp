package com.spbu.projecttrack.user.data.model

import com.spbu.projecttrack.projects.data.model.Project
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val projects: List<Project> = emptyList()
)
