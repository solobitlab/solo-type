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

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solotype.domain.AppVersion
import kotlinx.coroutines.delay

@Composable
fun SoloTypeApp(controller: SoloTypeController) {
    val focusRequester = remember { FocusRequester() }
    val state = controller.sessionState

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        while (true) {
            delay(100L)
            controller.onTick()
        }
    }

    SoloTypeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoloColors.BgPrimary)
                .focusRequester(focusRequester)
                .focusable()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopConfigBar(
                    detectedInfo = controller.detectedInfo,
                    selectedLayoutId = controller.selectedLayoutId,
                    selectedLanguage = controller.selectedLanguage,
                    lessons = controller.lessons,
                    selectedLesson = controller.selectedLesson,
                    ignoreAccents = controller.ignoreAccents,
                    showFingerColors = controller.showFingerColors,
                    onSelectLayout = {
                        controller.selectLayout(it)
                        focusRequester.requestFocus()
                    },
                    onSelectLanguage = {
                        controller.selectLanguage(it)
                        focusRequester.requestFocus()
                    },
                    onSelectLesson = {
                        controller.selectLesson(it)
                        focusRequester.requestFocus()
                    },
                    onToggleIgnoreAccents = {
                        controller.toggleIgnoreAccents()
                        focusRequester.requestFocus()
                    },
                    onToggleFingerColors = {
                        controller.toggleFingerColors()
                        focusRequester.requestFocus()
                    },
                    onRedetectKeyboard = {
                        controller.redetectKeyboard()
                        focusRequester.requestFocus()
                    },
                    onResetLesson = {
                        controller.resetSession()
                        focusRequester.requestFocus()
                    },
                    onMenuExpandedChange = { expanded ->
                        controller.isMenuExpanded = expanded
                        if (!expanded) {
                            focusRequester.requestFocus()
                        }
                    }
                )

                MetricsDashboard(state = state)

                TypingPromptDisplay(state = state)

                if (state.isCompleted) {
                    LessonSummaryCard(
                        state = state,
                        onRepeatLesson = {
                            controller.resetSession()
                            focusRequester.requestFocus()
                        },
                        onNextLesson = {
                            controller.nextLesson()
                            focusRequester.requestFocus()
                        }
                    )
                }

                VisualKeyboard(
                    layout = state.layout,
                    currentRequirement = state.currentRequirement,
                    lastWrongKeyId = state.lastWrongKeyPressed,
                    showFingerColors = controller.showFingerColors
                )

                HandsDiagram(
                    currentRequirement = state.currentRequirement,
                    fingerStats = state.fingerStats
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SoloType ${AppVersion.VERSION_NAME} • Solobit Labs",
                        color = SoloColors.TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Layout ativo: ${state.layout.id.displayName}",
                        color = SoloColors.TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
