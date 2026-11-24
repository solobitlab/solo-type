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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solotype.domain.Finger
import com.solotype.domain.FingerStats
import com.solotype.domain.Hand
import com.solotype.domain.KeyRequirement
import kotlin.math.roundToInt

@Composable
fun HandsDiagram(
    currentRequirement: KeyRequirement?,
    fingerStats: Map<Finger, FingerStats>,
    modifier: Modifier = Modifier
) {
    val leftFingers = listOf(
        Finger.LEFT_PINKY,
        Finger.LEFT_RING,
        Finger.LEFT_MIDDLE,
        Finger.LEFT_INDEX,
        Finger.LEFT_THUMB
    )
    val rightFingers = listOf(
        Finger.RIGHT_THUMB,
        Finger.RIGHT_INDEX,
        Finger.RIGHT_MIDDLE,
        Finger.RIGHT_RING,
        Finger.RIGHT_PINKY
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SingleHandCard(
            hand = Hand.LEFT,
            fingers = leftFingers,
            currentRequirement = currentRequirement,
            fingerStats = fingerStats,
            modifier = Modifier.weight(1f)
        )
        SingleHandCard(
            hand = Hand.RIGHT,
            fingers = rightFingers,
            currentRequirement = currentRequirement,
            fingerStats = fingerStats,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SingleHandCard(
    hand: Hand,
    fingers: List<Finger>,
    currentRequirement: KeyRequirement?,
    fingerStats: Map<Finger, FingerStats>,
    modifier: Modifier = Modifier
) {
    val activePrimary = currentRequirement?.primaryFinger
    val activeShift = if (currentRequirement?.requiresShift == true) currentRequirement.shiftFinger else null
    val hasActiveFingerOnHand = fingers.any { it == activePrimary || it == activeShift }

    Surface(
        color = SoloColors.SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (hasActiveFingerOnHand) 1.5.dp else 1.dp,
            color = if (hasActiveFingerOnHand) SoloColors.AccentPrimary.copy(alpha = 0.6f) else SoloColors.BorderSubtle
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hand.displayNamePt,
                    color = SoloColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                val activeLabel = when {
                    activePrimary?.hand == hand && activeShift?.hand == hand ->
                        "${activePrimary.shortNamePt} + Shift"
                    activePrimary?.hand == hand -> "Usar: ${activePrimary.shortNamePt}"
                    activeShift?.hand == hand -> "Shift: ${activeShift.shortNamePt}"
                    else -> "Posição base"
                }
                Text(
                    text = activeLabel,
                    color = if (hasActiveFingerOnHand) SoloColors.AccentPrimary else SoloColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (hasActiveFingerOnHand) FontWeight.Bold else FontWeight.Normal
                )
            }

            // Anatomical hand canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
            ) {
                val w = size.width
                val h = size.height
                val slotWidth = w / 5f
                val fingerWidth = (slotWidth * 0.48f).coerceAtMost(34f)
                val palmTop = h * 0.60f
                val palmLeft = slotWidth * 0.45f
                val palmWidth = w - slotWidth * 0.9f

                // Palm
                drawRoundRect(
                    color = SoloColors.SurfaceElevated,
                    topLeft = Offset(palmLeft, palmTop),
                    size = Size(palmWidth, h - palmTop - 4f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawRoundRect(
                    color = SoloColors.BorderSubtle,
                    topLeft = Offset(palmLeft, palmTop),
                    size = Size(palmWidth, h - palmTop - 4f),
                    cornerRadius = CornerRadius(14f, 14f),
                    style = Stroke(width = 1.5f)
                )

                fingers.forEachIndexed { idx, finger ->
                    val heightFactor = when (finger) {
                        Finger.LEFT_PINKY, Finger.RIGHT_PINKY -> 0.44f
                        Finger.LEFT_RING, Finger.RIGHT_RING -> 0.68f
                        Finger.LEFT_MIDDLE, Finger.RIGHT_MIDDLE -> 0.84f
                        Finger.LEFT_INDEX, Finger.RIGHT_INDEX -> 0.72f
                        Finger.LEFT_THUMB, Finger.RIGHT_THUMB -> 0.36f
                    }
                    val fingerHeight = (palmTop - 4f) * heightFactor
                    val isThumb = finger == Finger.LEFT_THUMB || finger == Finger.RIGHT_THUMB
                    val topY = if (isThumb) palmTop - fingerHeight * 0.55f else palmTop - fingerHeight + 6f
                    val centerX = slotWidth * (idx + 0.5f)
                    val leftX = centerX - fingerWidth / 2f

                    val isPrimary = activePrimary == finger
                    val isShift = activeShift == finger
                    val isActive = isPrimary || isShift
                    val baseColor = finger.uiColor()

                    if (isActive) {
                        drawRoundRect(
                            color = baseColor.copy(alpha = 0.25f),
                            topLeft = Offset(leftX - 5f, topY - 5f),
                            size = Size(fingerWidth + 10f, fingerHeight + 10f),
                            cornerRadius = CornerRadius(fingerWidth, fingerWidth)
                        )
                    }

                    drawRoundRect(
                        color = if (isActive) baseColor else baseColor.copy(alpha = 0.24f),
                        topLeft = Offset(leftX, topY),
                        size = Size(fingerWidth, fingerHeight),
                        cornerRadius = CornerRadius(fingerWidth / 2f, fingerWidth / 2f)
                    )
                    drawRoundRect(
                        color = if (isActive) Color.White else baseColor.copy(alpha = 0.75f),
                        topLeft = Offset(leftX, topY),
                        size = Size(fingerWidth, fingerHeight),
                        cornerRadius = CornerRadius(fingerWidth / 2f, fingerWidth / 2f),
                        style = Stroke(width = if (isActive) 2.5f else 1.2f)
                    )
                }
            }

            // Per-finger status badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                fingers.forEach { finger ->
                    val isPrimary = activePrimary == finger
                    val isShift = activeShift == finger
                    val isActive = isPrimary || isShift
                    val stats = fingerStats[finger]
                    val fingerColor = finger.uiColor()
                    val shortTitle = when (finger) {
                        Finger.LEFT_PINKY, Finger.RIGHT_PINKY -> "Mind."
                        Finger.LEFT_RING, Finger.RIGHT_RING -> "Anel."
                        Finger.LEFT_MIDDLE, Finger.RIGHT_MIDDLE -> "Médio"
                        Finger.LEFT_INDEX, Finger.RIGHT_INDEX -> "Indic."
                        Finger.LEFT_THUMB, Finger.RIGHT_THUMB -> "Poleg."
                    }

                    Surface(
                        color = if (isActive) fingerColor.copy(alpha = 0.26f) else SoloColors.SurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            width = if (isActive) 1.5.dp else 1.dp,
                            color = if (isActive) fingerColor else fingerColor.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = shortTitle,
                                color = if (isActive) Color.White else SoloColors.TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                            Text(
                                text = when {
                                    isPrimary -> "TECLA"
                                    isShift -> "SHIFT"
                                    stats != null && stats.total > 0 -> "${stats.accuracyPercent.roundToInt()}%"
                                    else -> "--"
                                },
                                color = when {
                                    isActive -> Color.White
                                    stats != null && stats.total > 0 -> fingerColor
                                    else -> SoloColors.TextMuted
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
