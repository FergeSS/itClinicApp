package com.spbu.projecttrack.projects.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.layout
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import projecttrack.composeapp.generated.resources.*
import com.spbu.projecttrack.projects.data.model.Project
import com.spbu.projecttrack.projects.data.model.Tag
import com.spbu.projecttrack.projects.presentation.components.SearchBar
import com.spbu.projecttrack.projects.presentation.components.FiltersAlert
import com.spbu.projecttrack.projects.presentation.models.ProjectFilters
import com.spbu.projecttrack.core.theme.AppColors
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: ProjectsViewModel,
    onProjectClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var filters by remember { mutableStateOf(ProjectFilters()) }
    val hasActiveFilters = filters.hasActiveFilters()
    val isAuthorized by com.spbu.projecttrack.core.auth.AuthManager.isAuthorized.collectAsState(initial = false)
    
    val openSansBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.Bold))
    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
    val titleColor = AppColors.Color3
    
    Box(modifier = modifier.fillMaxSize()) {
        // Лого СПбГУ на весь экран по ширине
        Image(
            painter = painterResource(Res.drawable.spbu_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .alpha(0.1f), // Видимость 10%
            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(Color.White), // Белый фон
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Белый фон навбара
                    .padding(top = 0.dp, bottom = 0.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Проекты",
                        fontFamily = openSansBold,
                        fontSize = 40.sp,
                        color = titleColor
                    )
                    // Индикатор авторизации
                    if (isAuthorized) {
                        Text(
                            text = "🔐 Авторизован",
                            fontFamily = openSansRegular,
                            fontSize = 12.sp,
                            color = AppColors.Color3.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Поиск
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                SearchBar(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    onFilterClick = { showFilters = true },
                    hasActiveFilters = hasActiveFilters,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Контент проектов
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is ProjectsUiState.Loading -> {
                        LoadingContent()
                    }
                    is ProjectsUiState.Success -> {
                        // Фильтруем проекты по поисковому запросу
                        val filteredProjects = if (searchText.isBlank()) {
                            state.projects
                        } else {
                            state.projects.filter { project ->
                                project.name.contains(searchText, ignoreCase = true) ||
                                project.shortDescription?.contains(searchText, ignoreCase = true) == true ||
                                project.description?.contains(searchText, ignoreCase = true) == true
                            }
                        }
                        
                        ProjectsContent(
                            projects = filteredProjects,
                            tags = state.tags,
                            isLoadingMore = state.isLoadingMore,
                            onProjectClick = onProjectClick,
                            onLoadMore = { viewModel.loadMoreProjects() }
                        )
                    }
                    is ProjectsUiState.Error -> {
                        ErrorContent(
                            message = state.message,
                            onRetry = { viewModel.retry() }
                        )
                    }
                }
            }
        }
        
        // FiltersAlert
        if (uiState is ProjectsUiState.Success) {
            FiltersAlert(
                isVisible = showFilters,
                onDismiss = { showFilters = false },
                tags = (uiState as ProjectsUiState.Success).tags,
                filters = filters,
                onFiltersChange = { filters = it }
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Загрузка проектов...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Ошибка загрузки",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Composable
private fun ProjectsContent(
    projects: List<Project>,
    tags: List<Tag>,
    isLoadingMore: Boolean,
    onProjectClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tagMap = tags.associateBy { it.id }
    
    if (projects.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет активных проектов",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 100.dp // Добавляем padding внизу чтобы последний проект был виден полностью
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                itemsIndexed(projects) { index, project ->
                    ProjectCard(
                        project = project,
                        tags = project.tags?.mapNotNull { tagMap[it] } ?: emptyList(),
                        onClick = { onProjectClick(project.slug ?: project.id) }
                    )
                    
                    // Загружаем следующую страницу когда осталось 3 элемента до конца
                    if (index >= projects.size - 3 && !isLoadingMore) {
                        onLoadMore()
                    }
                }
                
                // Индикатор загрузки внизу списка
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    tags: List<Tag>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val openSansBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.Bold))
    val openSansSemiBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.SemiBold))
    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
    val openSansMedium = FontFamily(Font(Res.font.opensans_bold, FontWeight.Medium))
    
    Card(
        modifier = modifier
            .width(375.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Прозрачный фон
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            // Полоска цвета 1 сверху
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(AppColors.Color1)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 12.dp)
            ) {
                // Дата слева (фиксированная ширина 50dp, без паддинга слева)
                Column(
                    modifier = Modifier.width(50.dp).padding(start = 0.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    val dateParts = formatDateForCard(project.dateStart ?: project.dateEnd ?: "")
                    if (dateParts.isNotEmpty()) {
                        Text(
                            text = dateParts.first(),
                            fontFamily = openSansBold,
                            fontSize = 20.sp,
                            color = AppColors.Color2
                        )
                        Text(
                            text = dateParts.drop(1).joinToString(" "),
                            fontFamily = openSansBold,
                            fontSize = 12.sp,
                            color = AppColors.Color2
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Контент справа
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Титул с заглушкой для статусов
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = project.name,
                            fontFamily = openSansBold,
                            fontSize = 16.sp,
                            color = AppColors.Color2,
                            modifier = Modifier.weight(1f)
                        )
                        // Заглушка для статусов (24x24)
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Описание (5 строк)
                    Text(
                        text = project.shortDescription ?: project.description ?: "",
                        fontFamily = openSansRegular,
                        fontSize = 10.sp,
                        color = AppColors.Color2,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 12.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Блок с 3 данными
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Вертикальная линия слева
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(AppColors.Color1)
                        )
                        
                        // Блок 1: Срок записи на проект (первая дата)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Срок записи",
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                            Text(
                                text = "на проект",
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.dateStart?.take(10) ?: "Не указано",
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                        }
                        
                        // Вертикальная полоска
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(AppColors.Color1)
                        )
                        
                        // Блок 2: Срок реализации проекта (вторая дата)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Срок реализации",
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                            Text(
                                text = "проекта",
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = project.dateEnd?.take(10) ?: "Не указано",
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                        }
                        
                        // Вертикальная полоска
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(AppColors.Color1)
                        )
                        
                        // Блок 3: Заказчик (во всю оставшуюся ширину)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Заказчик",
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Не указан", // TODO: получить из бэка
                                fontFamily = openSansSemiBold,
                                fontSize = 10.sp,
                                color = AppColors.Color2
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Теги проекта в несколько строк (высота блока 20)
                    // Упрощенная версия - просто показываем все теги в несколько строк
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Первая строка
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tags.take(3).forEach { tag ->
                                ProjectTagChip(tag = tag)
                            }
                        }
                        // Вторая строка если есть еще теги
                        if (tags.size > 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tags.drop(3).forEach { tag ->
                                    ProjectTagChip(tag = tag)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    tag: Tag,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = tag.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

private fun formatDate(dateString: String): String {
    // Simple date formatting - можно улучшить используя kotlinx-datetime
    return dateString.take(10)
}

private fun formatDateForCard(dateString: String): List<String> {
    if (dateString.isEmpty()) return emptyList()
    // Формат: "08 сен 2025" -> ["08", "сен", "2025"]
    val parts = dateString.take(10).split("-")
    if (parts.size == 3) {
        val day = parts[2]
        val month = when(parts[1]) {
            "01" -> "янв"
            "02" -> "фев"
            "03" -> "мар"
            "04" -> "апр"
            "05" -> "май"
            "06" -> "июн"
            "07" -> "июл"
            "08" -> "авг"
            "09" -> "сен"
            "10" -> "окт"
            "11" -> "ноя"
            "12" -> "дек"
            else -> parts[1]
        }
        val year = parts[0]
        return listOf(day, month, year)
    }
    return emptyList()
}

private fun formatDateRange(start: String?, end: String?): String {
    val startFormatted = start?.take(10) ?: ""
    val endFormatted = end?.take(10) ?: ""
    return when {
        startFormatted.isNotEmpty() && endFormatted.isNotEmpty() -> "$startFormatted - $endFormatted"
        startFormatted.isNotEmpty() -> "с $startFormatted"
        endFormatted.isNotEmpty() -> "до $endFormatted"
        else -> "Не указано"
    }
}

@Composable
private fun ProjectTagChip(
    tag: Tag,
    modifier: Modifier = Modifier
) {
    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = AppColors.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Color1)
    ) {
        Text(
            text = tag.name,
            fontFamily = openSansRegular,
            fontSize = 10.sp,
            color = AppColors.Color2,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

