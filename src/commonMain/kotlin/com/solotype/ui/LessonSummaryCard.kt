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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solotype.domain.TypingSessionState
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonSummaryCard(
    state: TypingSessionState,
    onRepeatLesson: () -> Unit,
    onNextLesson: () -> Unit
) {
    Surface(
        color = SoloColors.SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, SoloColors.AccentSuccess),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lição Concluída!",
                        color = SoloColors.AccentSuccess,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${state.lesson.title} — ${state.wpm.roundToInt()} PPM • ${formatOneDecimalPercent(state.accuracyPercent)} de acertos • ${state.errorKeystrokes} erros",
                        color = SoloColors.TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onRepeatLesson,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, SoloColors.BorderSubtle),
                        modifier = Modifier.focusProperties { canFocus = false }
                    ) {
                        Text(
                            text = "Repetir Lição",
                            color = SoloColors.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onNextLesson,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoloColors.AccentSuccess,
                            contentColor = SoloColors.BgPrimary
                        ),
                        modifier = Modifier.focusProperties { canFocus = false }
                    ) {
                        Text(
                            text = "Próxima Lição →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (state.fingerStats.isNotEmpty()) {
                Text(
                    text = "Desempenho por Dedo:",
                    color = SoloColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.fingerStats.entries
                        .sortedBy { it.key.ordinal }
                        .forEach { (finger, stats) ->
                            val fingerColor = finger.uiColor()
                            Surface(
                                color = fingerColor.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, fingerColor.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "${finger.shortNamePt}: ${formatOneDecimalPercent(stats.accuracyPercent)} (${stats.hits}/${stats.total})",
                                    color = SoloColors.TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                }
            }
        }
    }
}
