package com.audio.waveplayer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val surfaceAlpha = if (isDark) 0.35f else 0.65f
    val borderAlpha = if (isDark) 0.25f else 0.4f

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = surfaceAlpha),
                        MaterialTheme.colorScheme.surface.copy(alpha = surfaceAlpha * 0.7f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = borderAlpha),
                        Color.White.copy(alpha = borderAlpha * 0.2f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        content()
    }
}
