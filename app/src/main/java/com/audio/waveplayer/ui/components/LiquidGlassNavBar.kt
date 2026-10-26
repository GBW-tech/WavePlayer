package com.audio.waveplayer.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun LiquidGlassNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorOffset by animateDpAsState(
        targetValue = if (selectedTab == 0) 0.dp else 134.dp,
        animationSpec = spring(
            dampingRatio = 0.55f, // bouncy spring effect
            stiffness = 250f
        ),
        label = "GlassPillBounce"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 30 && selectedTab == 1) {
                        onTabSelected(0)
                    } else if (dragAmount < -30 && selectedTab == 0) {
                        onTabSelected(1)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(280.dp)
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Animated sliding glass indicator pill with spring bounce
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(134.dp)
                    .height(52.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassTabButton(
                    icon = Icons.Default.MusicNote,
                    label = "Library",
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f)
                )

                GlassTabButton(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun GlassTabButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
