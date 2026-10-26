package com.audio.waveplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SoundLabScreen(
    onBack: () -> Unit
) {
    var masterEnabled by remember { mutableStateOf(true) }
    var bassBoostStrength by remember { mutableFloatStateOf(40f) } // 0f to 100f
    var selectedPreset by remember { mutableStateOf("Bass Boost") }

    // 5-band Equalizer sliders (-15dB to +15dB)
    var band60Hz by remember { mutableFloatStateOf(4f) }
    var band230Hz by remember { mutableFloatStateOf(2f) }
    var band910Hz by remember { mutableFloatStateOf(0f) }
    var band3600Hz by remember { mutableFloatStateOf(1.5f) }
    var band14kHz by remember { mutableFloatStateOf(3f) }

    val presets = listOf("Flat", "Bass Boost", "Deep Bass", "Rock", "Pop", "Classical")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Sound Lab & Equalizer",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Master Enable Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Master DSP Engine",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Active software audio processing",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = masterEnabled,
                    onCheckedChange = { masterEnabled = it }
                )
            }
        }

        // Presets Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Sound Presets",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = selectedPreset == preset
                        val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(containerColor)
                                .clickable {
                                    selectedPreset = preset
                                    when (preset) {
                                        "Flat" -> {
                                            bassBoostStrength = 0f
                                            band60Hz = 0f; band230Hz = 0f; band910Hz = 0f; band3600Hz = 0f; band14kHz = 0f
                                        }
                                        "Bass Boost" -> {
                                            bassBoostStrength = 60f
                                            band60Hz = 6f; band230Hz = 4f; band910Hz = 0f; band3600Hz = 1f; band14kHz = 2f
                                        }
                                        "Deep Bass" -> {
                                            bassBoostStrength = 90f
                                            band60Hz = 10f; band230Hz = 6f; band910Hz = -1f; band3600Hz = 0f; band14kHz = 1f
                                        }
                                        "Rock" -> {
                                            bassBoostStrength = 50f
                                            band60Hz = 5f; band230Hz = 3f; band910Hz = -2f; band3600Hz = 4f; band14kHz = 5f
                                        }
                                        "Pop" -> {
                                            bassBoostStrength = 30f
                                            band60Hz = -1f; band230Hz = 2f; band910Hz = 5f; band3600Hz = 2f; band14kHz = -1f
                                        }
                                        "Classical" -> {
                                            bassBoostStrength = 10f
                                            band60Hz = 4f; band230Hz = 3f; band910Hz = 2f; band3600Hz = 3f; band14kHz = -2f
                                        }
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Bass Boost Slider Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Bass Boost Level",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${bassBoostStrength.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = bassBoostStrength,
                    onValueChange = {
                        bassBoostStrength = it
                        selectedPreset = "Custom"
                    },
                    valueRange = 0f..100f,
                    enabled = masterEnabled
                )
            }
        }

        // 5-Band Equalizer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "5-Band Equalizer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                EqBandSlider(label = "60 Hz", value = band60Hz, enabled = masterEnabled) { band60Hz = it; selectedPreset = "Custom" }
                EqBandSlider(label = "230 Hz", value = band230Hz, enabled = masterEnabled) { band230Hz = it; selectedPreset = "Custom" }
                EqBandSlider(label = "910 Hz", value = band910Hz, enabled = masterEnabled) { band910Hz = it; selectedPreset = "Custom" }
                EqBandSlider(label = "3.6 kHz", value = band3600Hz, enabled = masterEnabled) { band3600Hz = it; selectedPreset = "Custom" }
                EqBandSlider(label = "14 kHz", value = band14kHz, enabled = masterEnabled) { band14kHz = it; selectedPreset = "Custom" }
            }
        }
    }
}

@Composable
fun EqBandSlider(label: String, value: Float, enabled: Boolean, onChange: (Float) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            val formatted = kotlin.math.round(value * 10) / 10f
            Text(
                text = "$formatted dB",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = -15f..15f,
            enabled = enabled
        )
    }
}
