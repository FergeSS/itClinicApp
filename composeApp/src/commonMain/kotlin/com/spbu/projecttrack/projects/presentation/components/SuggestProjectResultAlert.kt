package com.spbu.projecttrack.projects.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spbu.projecttrack.core.theme.AppColors
import com.spbu.projecttrack.core.theme.AppFonts
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import projecttrack.composeapp.generated.resources.Res
import projecttrack.composeapp.generated.resources.close_icon
import projecttrack.composeapp.generated.resources.spbu_logo
import androidx.compose.material3.Text

@Composable
fun SuggestProjectResultAlert(
    isVisible: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 340.dp)
                .wrapContentHeight()
                .background(
                    color = AppColors.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = AppColors.Color1,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.spbu_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .alpha(1f)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        fontFamily = AppFonts.OpenSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = AppColors.Color2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    fontFamily = AppFonts.OpenSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 13.sp,
                    color = AppColors.Color1,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(30.dp)
                        .background(
                            color = AppColors.Color3,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = AppColors.BorderColor,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onDismiss
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ок",
                        fontFamily = AppFonts.OpenSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = AppColors.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SuggestProjectResultAlertSuccessPreview() {
    SuggestProjectResultAlert(
        isVisible = true,
        title = "Заявка отправлена",
        message = "Мы свяжемся с вами в ближайшее время.",
        onDismiss = {}
    )
}

@Preview
@Composable
private fun SuggestProjectResultAlertErrorPreview() {
    SuggestProjectResultAlert(
        isVisible = true,
        title = "Не удалось отправить заявку",
        message = "Попробуйте позже.",
        onDismiss = {}
    )
}
