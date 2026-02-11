package com.spbu.projecttrack.core.di

import com.spbu.projecttrack.core.network.HttpClientFactory
import com.spbu.projecttrack.projects.data.api.ContactRequestApi
import com.spbu.projecttrack.projects.data.api.ProjectsApi
import com.spbu.projecttrack.user.data.api.UserProfileApi
import com.spbu.projecttrack.projects.data.repository.ProjectsRepository
import com.spbu.projecttrack.projects.presentation.ProjectsViewModel
import com.spbu.projecttrack.projects.presentation.detail.ProjectDetailViewModel

object DependencyContainer {
    
    private val httpClient by lazy { HttpClientFactory.create() }
    
    private val projectsApi by lazy { ProjectsApi(httpClient) }
    private val contactRequestApi by lazy { ContactRequestApi(httpClient) }
    private val userProfileApi by lazy { UserProfileApi(httpClient) }
    
    private val projectsRepository by lazy { ProjectsRepository(projectsApi) }
    
    fun provideProjectsViewModel(): ProjectsViewModel {
        return ProjectsViewModel(projectsRepository)
    }
    
    fun provideProjectDetailViewModel(projectId: String): ProjectDetailViewModel {
        return ProjectDetailViewModel(projectsRepository, projectId)
    }

    fun provideContactRequestApi(): ContactRequestApi {
        return contactRequestApi
    }

    fun provideUserProfileApi(): UserProfileApi {
        return userProfileApi
    }
}
