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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solotype.domain.AppLanguage
import com.solotype.domain.AppVersion
import com.solotype.domain.DetectedKeyboardInfo
import com.solotype.domain.KeyboardLayoutId
import com.solotype.domain.TypingLesson

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopConfigBar(
    detectedInfo: DetectedKeyboardInfo,
    selectedLayoutId: KeyboardLayoutId,
    selectedLanguage: AppLanguage,
    lessons: List<TypingLesson>,
    selectedLesson: TypingLesson,
    ignoreAccents: Boolean,
    showFingerColors: Boolean,
    onSelectLayout: (KeyboardLayoutId) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectLesson: (TypingLesson) -> Unit,
    onToggleIgnoreAccents: () -> Unit,
    onToggleFingerColors: () -> Unit,
    onRedetectKeyboard: () -> Unit,
    onResetLesson: () -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit = {}
) {
    var lessonMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = SoloColors.SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SoloColors.BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header row: Brand + Detected Environment Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoloColors.AccentPrimary.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ST",
                            color = SoloColors.AccentPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "SoloType",
                                color = SoloColors.TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Surface(
                                color = SoloColors.AccentPrimary.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, SoloColors.AccentPrimary.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = AppVersion.VERSION_NAME,
                                    color = SoloColors.AccentPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Treinador de Digitação • Dedo Certo, Ordem Certa",
                            color = SoloColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Detected hardware, layout and locale pill
                Surface(
                    color = SoloColors.SurfaceElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SoloColors.BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SoloColors.AccentSuccess)
                        )
                        Column {
                            Text(
                                text = "${detectedInfo.osName} • ${detectedInfo.keyboardHardwareName}",
                                color = SoloColors.TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Detectado: ${detectedInfo.rawLayoutName} (${detectedInfo.rawLocaleCode}) • Fonte: ${detectedInfo.detectionSource}",
                                color = SoloColors.TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            color = SoloColors.AccentPrimary.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, SoloColors.AccentPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable { onRedetectKeyboard() }
                        ) {
                            Text(
                                text = "Redetectar",
                                color = SoloColors.AccentPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Selectors row: Layout, Language, Lesson, Toggles, Reset
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Layout chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Teclado:",
                        color = SoloColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    KeyboardLayoutId.entries.forEach { layoutId ->
                        val label = when (layoutId) {
                            KeyboardLayoutId.ABNT2 -> "ABNT2"
                            KeyboardLayoutId.US_QWERTY -> "US QWERTY"
                            KeyboardLayoutId.ES_ISO -> "ES ISO"
                        }
                        SelectorChip(
                            text = label,
                            selected = selectedLayoutId == layoutId,
                            onClick = { onSelectLayout(layoutId) }
                        )
                    }
                }

                // Language chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Idioma:",
                        color = SoloColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    AppLanguage.entries.forEach { lang ->
                        val label = when (lang) {
                            AppLanguage.PT_BR -> "Português"
                            AppLanguage.EN_US -> "English"
                            AppLanguage.ES_ES -> "Español"
                        }
                        SelectorChip(
                            text = label,
                            selected = selectedLanguage == lang,
                            onClick = { onSelectLanguage(lang) }
                        )
                    }
                }

                // Lesson Dropdown
                Box {
                    OutlinedButton(
                        onClick = {
                            lessonMenuExpanded = true
                            onMenuExpandedChange(true)
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, SoloColors.BorderSubtle),
                        modifier = Modifier.focusProperties { canFocus = false }
                    ) {
                        Text(
                            text = "Lição: ${selectedLesson.category.titlePt} ▾",
                            color = SoloColors.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    DropdownMenu(
                        expanded = lessonMenuExpanded,
                        onDismissRequest = {
                            lessonMenuExpanded = false
                            onMenuExpandedChange(false)
                        },
                        modifier = Modifier.background(SoloColors.SurfaceElevated)
                    ) {
                        lessons.forEach { lesson ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = lesson.category.titlePt,
                                            color = SoloColors.TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = if (lesson.id == selectedLesson.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = lesson.title,
                                            color = SoloColors.TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                onClick = {
                                    lessonMenuExpanded = false
                                    onMenuExpandedChange(false)
                                    onSelectLesson(lesson)
                                }
                            )
                        }
                    }
                }

                // Toggles & Reset
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SelectorChip(
                        text = "Simplificar Acentos",
                        selected = ignoreAccents,
                        onClick = onToggleIgnoreAccents
                    )
                    SelectorChip(
                        text = "Cores dos Dedos",
                        selected = showFingerColors,
                        onClick = onToggleFingerColors
                    )
                    Spacer(Modifier.width(2.dp))
                    OutlinedButton(
                        onClick = onResetLesson,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, SoloColors.AccentWarning.copy(alpha = 0.5f)),
                        modifier = Modifier.focusProperties { canFocus = false }
                    ) {
                        Text(
                            text = "Reiniciar (Esc)",
                            color = SoloColors.AccentWarning,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.focusProperties { canFocus = false },
        label = {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = SoloColors.SurfaceElevated,
            labelColor = SoloColors.TextSecondary,
            selectedContainerColor = SoloColors.AccentPrimary.copy(alpha = 0.2f),
            selectedLabelColor = SoloColors.AccentPrimary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = SoloColors.BorderSubtle,
            selectedBorderColor = SoloColors.AccentPrimary
        )
    )
}
