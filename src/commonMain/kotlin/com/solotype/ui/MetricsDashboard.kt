/*
 *   Copyright (c) 2026 Willian Santos
 *
 *   This work is licensed under the Creative Commons
 *   Attribution-NonCommercial 4.0 International License (CC BY-NC 4.0).
 *
 *   You are free to:
 *     - Share  — copy and redistribute the material in any medium or format
 *     - Adapt  — remix, transform, and build upon the material
 *
 *   Under the following terms:
 *     - Attribution    — You must give appropriate credit, provide a link to
 *                        the license, and indicate if changes were made.
 *     - NonCommercial  — You may not use the material for commercial purposes.
 *
 *   Owner rights:
 *     - Willian Santos retains all commercial rights.
 *     - The copyright holder may use, sell, sublicense, or relicense this
 *       work under different terms at any time.
 *
 *   Full license: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package com.solotype.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solotype.domain.TypingSessionState
import kotlin.math.roundToInt

@Composable
fun MetricsDashboard(state: TypingSessionState) {
    val accuracy = state.accuracyPercent
    val accuracyColor = when {
        accuracy >= 95.0 -> SoloColors.AccentSuccess
        accuracy >= 85.0 -> SoloColors.AccentWarning
        else -> SoloColors.AccentError
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard(
            label = "VELOCIDADE (PPM)",
            value = state.wpm.roundToInt().toString(),
            subtitle = "palavras/min",
            accentColor = SoloColors.AccentPrimary,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "CARACTERES/MIN",
            value = state.cpm.roundToInt().toString(),
            subtitle = "CPM líquido",
            accentColor = SoloColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "TAXA DE ACERTOS",
            value = formatOneDecimalPercent(accuracy),
            subtitle = "${state.correctKeystrokes}/${state.totalKeystrokes} teclas",
            accentColor = accuracyColor,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "ERROS",
            value = state.errorKeystrokes.toString(),
            subtitle = if (state.errorKeystrokes == 0) "perfeito!" else "teclas erradas",
            accentColor = if (state.errorKeystrokes == 0) SoloColors.AccentSuccess else SoloColors.AccentError,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "SEQUÊNCIA",
            value = "${state.currentStreak}",
            subtitle = "recorde: ${state.bestStreak}",
            accentColor = SoloColors.AccentWarning,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            label = "TEMPO",
            value = formatElapsed(state.elapsedMillis),
            subtitle = "${(state.progressPercent * 100f).roundToInt()}% concluído",
            accentColor = SoloColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SoloColors.SurfaceCard,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SoloColors.BorderSubtle),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                color = SoloColors.TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Text(
                text = value,
                color = accentColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                color = SoloColors.TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

fun formatOneDecimalPercent(value: Double): String {
    val tenths = (value * 10.0).roundToInt()
    val whole = tenths / 10
    val frac = tenths % 10
    return "$whole.$frac%"
}

fun formatElapsed(millis: Long): String {
    val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    val minStr = minutes.toString().padStart(2, '0')
    val secStr = seconds.toString().padStart(2, '0')
    return "$minStr:$secStr"
}
