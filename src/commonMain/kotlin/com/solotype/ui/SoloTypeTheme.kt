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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.solotype.domain.Finger

object SoloColors {
    val BgPrimary = Color(0xFF0B1120)
    val BgSecondary = Color(0xFF111827)
    val SurfaceCard = Color(0xFF151E32)
    val SurfaceElevated = Color(0xFF1E293B)
    val SurfaceKey = Color(0xFF1A253A)
    val BorderSubtle = Color(0xFF334155)
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val TextMuted = Color(0xFF64748B)
    val AccentPrimary = Color(0xFF38BDF8)
    val AccentSuccess = Color(0xFF10B981)
    val AccentWarning = Color(0xFFF59E0B)
    val AccentError = Color(0xFFEF4444)
}

fun Finger.uiColor(): Color = Color(colorHex)

private val SoloDarkColorScheme = darkColorScheme(
    primary = SoloColors.AccentPrimary,
    onPrimary = SoloColors.BgPrimary,
    secondary = SoloColors.AccentSuccess,
    background = SoloColors.BgPrimary,
    onBackground = SoloColors.TextPrimary,
    surface = SoloColors.SurfaceCard,
    onSurface = SoloColors.TextPrimary,
    surfaceVariant = SoloColors.SurfaceElevated,
    onSurfaceVariant = SoloColors.TextSecondary,
    error = SoloColors.AccentError,
    outline = SoloColors.BorderSubtle
)

@Composable
fun SoloTypeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SoloDarkColorScheme,
        content = content
    )
}
