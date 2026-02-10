package com.spbu.projecttrack

import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import com.spbu.projecttrack.core.di.DependencyContainer
import com.spbu.projecttrack.core.storage.createAppPreferences
import com.spbu.projecttrack.main.presentation.MainScreen
import com.spbu.projecttrack.onboarding.presentation.OnboardingScreen
import com.spbu.projecttrack.projects.presentation.detail.ProjectDetailScreen
import kotlinx.coroutines.launch

sealed class Screen {
    data object Onboarding : Screen()
    data object Main : Screen()
    data class ProjectDetail(val projectId: String) : Screen()
}

@Composable
actual fun App() {
    MaterialTheme {
        val preferences = remember { createAppPreferences() }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        
        // Логируем конфигурацию API при запуске
        LaunchedEffect(Unit) {
            println(com.spbu.projecttrack.core.network.ApiConfig.getDebugInfo())
        }
        
        var currentScreen by remember {
            mutableStateOf<Screen>(
                // TODO: Для тестов - всегда показываем onboarding
                Screen.Onboarding
                // if (preferences.isOnboardingCompleted()) Screen.Main else Screen.Onboarding
            )
        }
        
        // BackHandler для обработки системного жеста "назад"
        BackHandler(enabled = currentScreen !is Screen.Onboarding) {
            when (currentScreen) {
                is Screen.ProjectDetail -> currentScreen = Screen.Main
                is Screen.Main -> currentScreen = Screen.Onboarding
                else -> { /* На Onboarding ничего не делаем */ }
            }
        }
        
        when (val screen = currentScreen) {
            is Screen.Onboarding -> {
                OnboardingScreen(
                    onGitHubAuth = {
                        // Тестовая логика: устанавливаем токен для работы с локальным бэкендом
                        com.spbu.projecttrack.core.auth.AuthManager.setTestToken()
                        preferences.setOnboardingCompleted()
                        currentScreen = Screen.Main
                        scope.launch {
                            snackbarHostState.showSnackbar("Авторизация выполнена (тестовый режим)")
                        }
                    },
                    onContinueWithoutAuth = {
                        // Без авторизации - работаем с публичными эндпоинтами
                        preferences.setOnboardingCompleted()
                        currentScreen = Screen.Main
                    }
                )
            }
            is Screen.Main -> {
                MainScreen(
                    onProjectDetailClick = { projectId ->
                        currentScreen = Screen.ProjectDetail(projectId)
                    }
                )
            }
            is Screen.ProjectDetail -> {
                val detailViewModel = remember(screen.projectId) {
                    DependencyContainer.provideProjectDetailViewModel(screen.projectId)
                }
                ProjectDetailScreen(
                    viewModel = detailViewModel,
                    onBackClick = {
                        currentScreen = Screen.Main
                    }
                )
            }
        }
        
        // Snackbar для уведомлений
        SnackbarHost(hostState = snackbarHostState)
    }
}
