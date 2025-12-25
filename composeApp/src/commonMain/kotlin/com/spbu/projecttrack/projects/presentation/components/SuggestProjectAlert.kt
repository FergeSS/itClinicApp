package com.spbu.projecttrack.projects.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spbu.projecttrack.core.theme.AppColors
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import projecttrack.composeapp.generated.resources.*

@Composable
fun SuggestProjectAlert(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit, // name, email
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    // Кроссфейд анимация
    androidx.compose.animation.AnimatedVisibility(
        visible = isVisible,
        enter = androidx.compose.animation.fadeIn(
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
        ),
        exit = androidx.compose.animation.fadeOut(
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
        )
    ) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onDismiss)
            ) {
            Box(
                modifier = Modifier
                    .width(350.dp)
                    .wrapContentHeight() // Динамическая высота
                    .align(Alignment.Center)
                    .clickable(enabled = false) { }
            ) {
                // Фон логотип СПбГУ - по центру, может обрезаться
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = AppColors.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = AppColors.Color1,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.spbu_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.1f), // Видимость 10%
                        contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
                    )
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    // Заголовок по центру с кнопкой закрытия
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val openSansBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.Bold))
                        Text(
                            text = "Заказчикам",
                            fontFamily = openSansBold,
                            fontSize = 24.sp,
                            color = AppColors.Color2,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        
                        Image(
                            painter = painterResource(Res.drawable.close_icon),
                            contentDescription = "Закрыть",
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterEnd)
                                .clickable(onClick = onDismiss)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Текст
                    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
                    Text(
                        text = "Если у Вас есть запрос на сотрудничество и создание проекта, заполните онлайн-заявку. Наш представитель свяжется с вами в ближайшее время",
                        fontFamily = openSansRegular,
                        fontSize = 12.sp,
                        color = AppColors.Color1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Поле Имя
                    TextFieldWithLabel(
                        label = "Имя",
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Имя"
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Поле Почта
                    TextFieldWithLabel(
                        label = "Почта",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Почта"
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Кнопка Отправить без затемнения
                    val openSansSemiBold = FontFamily(Font(Res.font.opensans_bold, FontWeight.SemiBold))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                color = AppColors.Color3,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = AppColors.BorderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(
                                indication = null, // Убираем затемнение
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                onClick = {
                                    onSubmit(name, email)
                                    onDismiss()
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Отправить",
                            fontFamily = openSansSemiBold,
                            fontSize = 15.sp,
                            color = AppColors.White
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val openSansRegular = FontFamily(Font(Res.font.opensans_bold, FontWeight.Normal))
    
    Column(modifier = modifier) {
        Text(
            text = label,
            fontFamily = openSansRegular,
            fontSize = 12.sp,
            color = AppColors.Color2,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Текстфилд 200x36 с полосой снизу во всю ширину 2 цвета
        Column {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .width(200.dp)
                    .height(36.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = openSansRegular,
                    fontSize = 12.sp,
                    color = if (value.isEmpty()) AppColors.Color1 else AppColors.Color2
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontFamily = openSansRegular,
                                fontSize = 12.sp,
                                color = AppColors.Color1
                            )
                        }
                        innerTextField()
                    }
                }
            )
            
            // Полоса снизу во всю ширину 2 цвета
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppColors.Color2)
            )
        }
    }
}

