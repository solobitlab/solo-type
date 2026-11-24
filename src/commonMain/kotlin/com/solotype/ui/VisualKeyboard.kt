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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solotype.domain.KeyCap
import com.solotype.domain.KeyRequirement
import com.solotype.domain.KeyboardLayout

@Composable
fun VisualKeyboard(
    layout: KeyboardLayout,
    currentRequirement: KeyRequirement?,
    lastWrongKeyId: String?,
    showFingerColors: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SoloColors.SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SoloColors.BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Teclado Visual — ${layout.id.displayName}",
                    color = SoloColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "F e J têm marcação tátil para os indicadores",
                    color = SoloColors.TextMuted,
                    fontSize = 11.sp
                )
            }

            layout.rows.forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    rowKeys.forEach { keyCap ->
                        val isPrimaryTarget = currentRequirement?.primaryKeyId == keyCap.id
                        val isShiftTarget = currentRequirement?.requiresShift == true &&
                            currentRequirement.shiftKeyId == keyCap.id
                        val isWrongKey = lastWrongKeyId == keyCap.id && !isPrimaryTarget && !isShiftTarget

                        VisualKeyCap(
                            keyCap = keyCap,
                            isTarget = isPrimaryTarget || isShiftTarget,
                            isShiftTarget = isShiftTarget,
                            isWrongKey = isWrongKey,
                            showFingerColors = showFingerColors,
                            modifier = Modifier.weight(keyCap.widthWeight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualKeyCap(
    keyCap: KeyCap,
    isTarget: Boolean,
    isShiftTarget: Boolean,
    isWrongKey: Boolean,
    showFingerColors: Boolean,
    modifier: Modifier = Modifier
) {
    val fingerColor = keyCap.finger.uiColor()
    val bgColor: Color
    val borderColor: Color
    val borderWidth: Float
    val textColor: Color

    when {
        isTarget -> {
            bgColor = fingerColor.copy(alpha = 0.38f)
            borderColor = fingerColor
            borderWidth = 2.5f
            textColor = Color.White
        }
        isWrongKey -> {
            bgColor = SoloColors.AccentError.copy(alpha = 0.35f)
            borderColor = SoloColors.AccentError
            borderWidth = 2f
            textColor = Color.White
        }
        showFingerColors -> {
            bgColor = fingerColor.copy(alpha = if (keyCap.isModifier) 0.08f else 0.14f)
            borderColor = fingerColor.copy(alpha = if (keyCap.isModifier) 0.30f else 0.50f)
            borderWidth = 1f
            textColor = if (keyCap.isModifier) SoloColors.TextSecondary else SoloColors.TextPrimary
        }
        else -> {
            bgColor = SoloColors.SurfaceKey
            borderColor = SoloColors.BorderSubtle
            borderWidth = 1f
            textColor = if (keyCap.isModifier) SoloColors.TextSecondary else SoloColors.TextPrimary
        }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(7.dp),
        border = BorderStroke(borderWidth.dp, borderColor),
        modifier = modifier.height(48.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp)
        ) {
            if (keyCap.shiftLabel != null) {
                Text(
                    text = keyCap.shiftLabel,
                    color = if (isTarget) Color.White.copy(alpha = 0.9f) else SoloColors.TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Text(
                text = if (isShiftTarget) "SHIFT" else keyCap.primaryLabel,
                color = textColor,
                fontSize = if (keyCap.primaryLabel.length > 2) 11.sp else 14.sp,
                fontWeight = if (isTarget) FontWeight.ExtraBold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.align(
                    if (keyCap.shiftLabel != null) Alignment.BottomEnd else Alignment.Center
                )
            )

            if (keyCap.isHomeKeyBump) {
                Spacer(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(12.dp)
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (isTarget) Color.White else fingerColor)
                )
            }
        }
    }
}
