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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solotype.domain.CharState
import com.solotype.domain.TypingSessionState

@Composable
fun TypingPromptDisplay(state: TypingSessionState) {
    val requirement = state.currentRequirement
    val targetChar = state.currentTargetChar

    Surface(
        color = SoloColors.SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SoloColors.BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Lesson title & Finger guidance banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.lesson.title,
                        color = SoloColors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.lesson.description,
                        color = SoloColors.TextSecondary,
                        fontSize = 12.sp
                    )
                }

                if (requirement != null && targetChar != null) {
                    val primaryColor = requirement.primaryFinger.uiColor()
                    val keyDisplay = when (targetChar) {
                        ' ' -> "Espaço"
                        '\n' -> "Enter"
                        '\t' -> "Tab"
                        else -> "'$targetChar'"
                    }

                    Surface(
                        color = primaryColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, primaryColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Tecla: [ $keyDisplay ]",
                                color = SoloColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                            val fingerInstruction = buildString {
                                append("Dedo: ${requirement.primaryFinger.displayNamePt}")
                                val shiftFinger = requirement.shiftFinger
                                if (requirement.requiresShift && shiftFinger != null) {
                                    append(" + Shift (${shiftFinger.shortNamePt})")
                                }
                            }
                            Text(
                                text = fingerInstruction,
                                color = primaryColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Error guidance banner when wrong key was pressed
            val lastExpected = state.lastExpectedRequirement
            if (state.lastWrongKeyPressed != null && lastExpected != null) {
                val expectedLabel = if (lastExpected.primaryKeyId == "SPACE") "Espaço" else lastExpected.primaryKeyLabel
                val shiftText = lastExpected.shiftFinger?.let { " + Shift (${it.shortNamePt})" } ?: ""
                Surface(
                    color = SoloColors.AccentError.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SoloColors.AccentError.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tecla incorreta! Pressione [ $expectedLabel ] usando ${lastExpected.primaryFinger.displayNamePt}$shiftText na ordem certa.",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }

            // Text prompt box with character-level status highlighting
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SoloColors.BgPrimary)
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                val annotatedPrompt = buildAnnotatedString {
                    state.lesson.text.forEachIndexed { index, ch ->
                        val charState = state.charStates.getOrElse(index) { CharState.PENDING }
                        val displayChar = if (ch == ' ' && (charState == CharState.CURRENT || charState == CharState.CURRENT_ERROR)) {
                            '␣'
                        } else {
                            ch
                        }
                        val style = when (charState) {
                            CharState.CORRECT_FIRST_TRY -> SpanStyle(
                                color = SoloColors.AccentSuccess
                            )
                            CharState.CORRECTED -> SpanStyle(
                                color = SoloColors.AccentWarning
                            )
                            CharState.CURRENT -> SpanStyle(
                                color = SoloColors.BgPrimary,
                                background = SoloColors.AccentPrimary,
                                fontWeight = FontWeight.ExtraBold
                            )
                            CharState.CURRENT_ERROR -> SpanStyle(
                                color = Color.White,
                                background = SoloColors.AccentError,
                                fontWeight = FontWeight.ExtraBold,
                                textDecoration = TextDecoration.Underline
                            )
                            CharState.PENDING -> SpanStyle(
                                color = SoloColors.TextSecondary
                            )
                        }
                        withStyle(style) {
                            append(displayChar)
                        }
                    }
                }

                Text(
                    text = annotatedPrompt,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 23.sp,
                    lineHeight = 34.sp,
                    letterSpacing = 0.6.sp
                )
            }

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LinearProgressIndicator(
                    progress = { state.progressPercent.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SoloColors.AccentPrimary,
                    trackColor = SoloColors.SurfaceElevated
                )
                Text(
                    text = "${state.currentIndex}/${state.lesson.text.length}",
                    color = SoloColors.TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
