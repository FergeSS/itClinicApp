package com.spbu.projecttrack.main.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.spbu.projecttrack.core.auth.AuthManager
import com.spbu.projecttrack.core.di.DependencyContainer
import com.spbu.projecttrack.projects.presentation.ProjectsScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import projecttrack.composeapp.generated.resources.*

// Custom TabBar colors
private val TabBarBackground = Color(0xFF9F2D20)
private val TabBarBorder = Color(0xFFCF3F2F)
private val UnselectedTabColor = Color(0xFFA6A6A6)
private val SelectedTabColor = Color.White
private val SelectionGradientTop = Color(0xFFCF3F2F)
private val SelectionGradientBottom = Color(0xFFB13123)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onProjectDetailClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showSuggestProject by remember { mutableStateOf(false) }
    val isAuthorized by AuthManager.isAuthorized.collectAsState(initial = false)

    MainScreenContent(
        onProjectDetailClick = onProjectDetailClick,
        modifier = modifier,
        isAuthorized = isAuthorized,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        showSuggestProject = showSuggestProject,
        onShowSuggestProjectChange = { showSuggestProject = it }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    onProjectDetailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isAuthorized: Boolean,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    showSuggestProject: Boolean,
    onShowSuggestProjectChange: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = Color.White, // Белый фон для Scaffold
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Таббар
                CustomTabBar(
                    selectedTab = selectedTab,
                    onTabSelected = { onTabSelected(it) }
                )

                // Кнопки над таббаром по оси Y, привязаны к правому краю таббара.
                // Важно: "Предложить проект" всегда в одном месте; "Мой проект" при появлении рисуем выше,
                // не влияя на позицию кнопки ниже.
                if (selectedTab == 0) {
                    val density = LocalDensity.current
                    var suggestBtnHeightPx by remember { mutableStateOf(0) }
                    val suggestBtnHeightDp = if (suggestBtnHeightPx == 0) {
                        46.dp // fallback, если высота ещё не измерена
                    } else {
                        with(density) { suggestBtnHeightPx.toDp() }
                    }

                    Box(
                        modifier = Modifier
                            .width(380.dp) // Ширина таббара
                            .offset(y = (-95).dp), // Фиксированная позиция для кнопки "Предложить проект"
                        contentAlignment = Alignment.TopEnd
                    ) {
                        // Кнопка "Предложить проект" — всегда на одном месте
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .onSizeChanged { suggestBtnHeightPx = it.height }
                        ) {
                            com.spbu.projecttrack.projects.presentation.components.SuggestProjectButton(
                                onClick = { onShowSuggestProjectChange(true) }
                            )
                        }

                        // Кнопка "Мой проект" (если авторизован) — просто выше
                        if (isAuthorized) {
                            com.spbu.projecttrack.projects.presentation.components.MyProjectButton(
                                onClick = { /* TODO: navigate to my project */ },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(y = -(suggestBtnHeightDp + 10.dp))
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val isPreview = LocalInspectionMode.current

            when (selectedTab) {
                0 -> {
                    if (isPreview) {
                        // В превью не дергаем DI/VM
                        Box(modifier = Modifier.fillMaxSize())
                    } else {
                        val projectsViewModel = remember { DependencyContainer.provideProjectsViewModel() }
                        ProjectsScreen(
                            viewModel = projectsViewModel,
                            onProjectClick = onProjectDetailClick
                        )
                    }
                }

                1 -> RankingScreen()
                2 -> InfoScreen()
            }

            // Алерт "Предложить проект"
            com.spbu.projecttrack.projects.presentation.components.SuggestProjectAlert(
                isVisible = showSuggestProject,
                onDismiss = { onShowSuggestProjectChange(false) },
                onSubmit = { name, email ->
                    // TODO: Отправить запрос в бэк
                }
            )
        }
    }
}

@Composable
internal fun CustomTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Calculate animated offset for selection indicator
    val baseOffset = when (selectedTab) {
        0 -> 5.dp
        1 -> 128.5.dp
        2 -> 252.dp
        else -> 5.dp
    }

    val offsetX by animateDpAsState(
        targetValue = baseOffset + with(density) { dragOffset.toDp() },
        animationSpec = if (isDragging) tween(durationMillis = 0) else tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .offset(y = (-30).dp), // Поднимаем таббар на 30dp вверх
        contentAlignment = Alignment.Center
    ) {
        // Main TabBar container
        Box(
            modifier = Modifier
                .width(380.dp)
                .height(60.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(30.dp), // Скругление 30dp
                    ambientColor = Color.Black.copy(alpha = 0.55f),
                    spotColor = Color.Black.copy(alpha = 0.55f),
                    clip = false
                )
                .border(
                    width = 2.dp,
                    color = TabBarBorder,
                    shape = RoundedCornerShape(30.dp) // Скругление 30dp
                )
                .background(
                    color = TabBarBackground,
                    shape = RoundedCornerShape(30.dp) // Скругление 30dp
                )
        ) {
            // Animated selection indicator with drag support
            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = 5.dp)
                    .width(123.dp)
                    .height(50.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { _: Offset -> isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                // Определяем ближайшую вкладку на основе позиции
                                val tabWidth = with(density) { 123.dp.toPx() }
                                val currentPosition = with(density) { baseOffset.toPx() } + dragOffset
                                val newTab = when {
                                    currentPosition < tabWidth * 0.5f -> 0
                                    currentPosition < tabWidth * 1.5f -> 1
                                    else -> 2
                                }
                                if (newTab != selectedTab) {
                                    onTabSelected(newTab)
                                }
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                isDragging = false
                                dragOffset = 0f
                            }
                        ) { _, dragAmount ->
                            dragOffset += dragAmount
                            // Ограничиваем перетаскивание в пределах таббара
                            val minOffset = with(density) { -baseOffset.toPx() }
                            val maxOffset = with(density) {
                                (380.dp.toPx() - baseOffset.toPx() - 123.dp.toPx())
                            }
                            dragOffset = dragOffset.coerceIn(minOffset, maxOffset)
                        }
                    }
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(25.dp),
                        ambientColor = Color.Black.copy(alpha = 0.4f),
                        spotColor = Color.Black.copy(alpha = 0.4f),
                        clip = false
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SelectionGradientTop.copy(alpha = 0.7f),
                                SelectionGradientBottom.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
            )

            // Tab items
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Projects (SPbU logo)
                TabItem(
                    icon = Res.drawable.spbu_tab_logo,
                    iconSize = Pair(33.dp, 41.dp),
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                )

                // Tab 2: Statistics
                TabItem(
                    icon = Res.drawable.stats_tab_logo,
                    iconSize = Pair(30.dp, 27.5.dp),
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )

                // Tab 3: Settings
                TabItem(
                    icon = Res.drawable.settings_tab_logo,
                    iconSize = Pair(30.dp, 30.dp),
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) }
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    icon: org.jetbrains.compose.resources.DrawableResource,
    iconSize: Pair<androidx.compose.ui.unit.Dp, androidx.compose.ui.unit.Dp>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1f, // Увеличение на 15% при нажатии
        animationSpec = tween(durationMillis = 150)
    )

    Box(
        modifier = modifier
            .width(123.dp)
            .height(50.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Убираем стандартное затемнение
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(width = iconSize.first, height = iconSize.second)
                .scale(scale),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                if (isSelected) SelectedTabColor else UnselectedTabColor
            )
        )
    }
}

@Preview(showBackground = true, name = "Main (Unauthorized)")
@Composable
private fun MainScreenPreview_Unauthorized() {
    MainScreenContent(
        onProjectDetailClick = {},
        modifier = Modifier,
        isAuthorized = false,
        selectedTab = 0,
        onTabSelected = {},
        showSuggestProject = false,
        onShowSuggestProjectChange = {}
    )
}

@Preview(showBackground = true, name = "Main (Authorized)")
@Composable
private fun MainScreenPreview_Authorized() {
    MainScreenContent(
        onProjectDetailClick = {},
        modifier = Modifier,
        isAuthorized = true,
        selectedTab = 0,
        onTabSelected = {},
        showSuggestProject = false,
        onShowSuggestProjectChange = {}
    )
}
