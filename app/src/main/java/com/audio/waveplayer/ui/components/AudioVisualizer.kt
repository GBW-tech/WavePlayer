package com.audio.waveplayer.ui.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Visualizer")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Phase"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val maxHeight = size.height
        if (maxHeight <= 0f) return@Canvas

        val barCount = 12
        val totalWidth = size.width
        val spacing = 6.dp.toPx()
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = (totalWidth - totalSpacing) / barCount

        for (i in 0 until barCount) {
            val x = i * (barWidth + spacing)
            val freqFactor = (i + 1) * 0.5f
            val multiplier = if (isPlaying) {
                (sin(phase * freqFactor + i * 0.7f) + 1f) / 2f * 0.75f + 0.25f
            } else {
                0.12f
            }
            val barHeight = (maxHeight * multiplier).coerceIn(4.dp.toPx(), maxHeight)

            drawRoundRect(
                color = primaryColor.copy(alpha = 0.35f + ((i % 4) * 0.1f)),
                topLeft = Offset(x, maxHeight - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
