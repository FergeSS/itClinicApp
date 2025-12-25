package com.spbu.projecttrack.projects.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spbu.projecttrack.projects.data.model.*
import com.spbu.projecttrack.projects.data.repository.ProjectsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProjectDetailUiState {
    data object Loading : ProjectDetailUiState()
    data class Success(
        val project: ProjectDetail,
        val tags: List<Tag>,
        val teams: List<Team>,
        val members: List<Member>,
        val users: List<User>
    ) : ProjectDetailUiState()
    data class Error(val message: String) : ProjectDetailUiState()
}

class ProjectDetailViewModel(
    private val repository: ProjectsRepository,
    private val projectId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProjectDetailUiState>(ProjectDetailUiState.Loading)
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadProjectDetail()
    }
    
    fun loadProjectDetail() {
        viewModelScope.launch {
            _uiState.value = ProjectDetailUiState.Loading
            
            repository.getProjectById(projectId)
                .onSuccess { response ->
                    val project = response.project  // Теперь это одиночный проект
                    if (project != null) {
                        _uiState.value = ProjectDetailUiState.Success(
                            project = project,
                            tags = response.tags,
                            teams = response.teams ?: emptyList(),
                            members = response.members ?: emptyList(),
                            users = response.users ?: emptyList()
                        )
                    } else {
                        _uiState.value = ProjectDetailUiState.Error("Проект не найден")
                    }
                }
                .onFailure { error ->
                    _uiState.value = ProjectDetailUiState.Error(
                        message = error.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }
    
    fun retry() {
        loadProjectDetail()
    }
}

