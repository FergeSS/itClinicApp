package com.spbu.projecttrack.projects.presentation.myproject

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spbu.projecttrack.core.theme.AppColors
import com.spbu.projecttrack.projects.data.model.Project
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import projecttrack.composeapp.generated.resources.*

/**
 * Экран "Мой проект" для авторизованного пользователя
 * Дизайн как детали проекта, но без кнопки назад
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectScreen(
    project: Project?,
    onBackToProjects: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openSansBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.Bold))
    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White) // Белый фон на весь экран включая статус-бар
    ) {
        // Лого СПбГУ на весь экран (включая под таббаром)
        Image(
            painter = painterResource(Res.drawable.spbu_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(1f),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )
        
        Scaffold(
            containerColor = AppColors.White, // Белый фон
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Мой проект",
                            fontFamily = openSansBold,
                            fontSize = 24.sp,
                            color = AppColors.Color2
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.White
                    )
                )
            }
        ) { paddingValues ->
            if (project == null) {
                // Нет проекта - показываем заглушку
                NoProjectContent(
                    onBackToProjects = onBackToProjects,
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                // Есть проект - показываем детали
                ProjectDetailsContent(
                    project = project,
                    onBackToProjects = onBackToProjects,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun NoProjectContent(
    onBackToProjects: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openSansSemiBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.SemiBold))
    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "У вас пока нет проекта",
                fontFamily = openSansSemiBold,
                fontSize = 20.sp,
                color = AppColors.Color2
            )
            
            Text(
                text = "Вы еще не участвуете ни в одном проекте.\nПросмотрите доступные проекты и подайте заявку!",
                fontFamily = openSansRegular,
                fontSize = 14.sp,
                color = AppColors.Color1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Кнопка "Все проекты"
            com.spbu.projecttrack.projects.presentation.components.SuggestProjectButton(
                onClick = onBackToProjects,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun ProjectDetailsContent(
    project: Project,
    onBackToProjects: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openSansBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.Bold))
    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Название проекта
        Text(
            text = project.name,
            fontFamily = openSansBold,
            fontSize = 24.sp,
            color = AppColors.Color2
        )
        
        // Описание
        project.description?.let { description ->
            Text(
                text = description,
                fontFamily = openSansRegular,
                fontSize = 14.sp,
                color = AppColors.Color2
            )
        }
        
        // Даты
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Срок записи",
                    fontFamily = openSansRegular,
                    fontSize = 12.sp,
                    color = AppColors.Color1
                )
                Text(
                    text = project.dateStart ?: "—",
                    fontFamily = openSansRegular,
                    fontSize = 14.sp,
                    color = AppColors.Color2
                )
            }
            
            Column {
                Text(
                    text = "Срок реализации",
                    fontFamily = openSansRegular,
                    fontSize = 12.sp,
                    color = AppColors.Color1
                )
                Text(
                    text = project.dateEnd ?: "—",
                    fontFamily = openSansRegular,
                    fontSize = 14.sp,
                    color = AppColors.Color2
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Кнопка "Все проекты" внизу
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            com.spbu.projecttrack.projects.presentation.components.SuggestProjectButton(
                onClick = onBackToProjects
            )
        }
    }
}







