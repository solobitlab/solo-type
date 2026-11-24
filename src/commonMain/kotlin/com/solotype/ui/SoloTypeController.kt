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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.solotype.domain.AppLanguage
import com.solotype.domain.DetectedKeyboardInfo
import com.solotype.domain.KeyboardEnvironmentDetector
import com.solotype.domain.KeyboardLayoutId
import com.solotype.domain.KeyboardLayouts
import com.solotype.domain.LessonRepository
import com.solotype.domain.TypingEngine
import com.solotype.domain.TypingLesson
import com.solotype.domain.TypingSessionState
import com.solotype.platform.currentTimeMillis

class SoloTypeController(
    private val detector: KeyboardEnvironmentDetector
) {
    var detectedInfo: DetectedKeyboardInfo by mutableStateOf(detector.detect())
        private set

    var selectedLayoutId: KeyboardLayoutId by mutableStateOf(detectedInfo.detectedLayout)
        private set

    var selectedLanguage: AppLanguage by mutableStateOf(detectedInfo.detectedLanguage)
        private set

    var lessons: List<TypingLesson> by mutableStateOf(
        LessonRepository.lessonsFor(selectedLanguage, selectedLayoutId)
    )
        private set

    var selectedLesson: TypingLesson by mutableStateOf(lessons.first())
        private set

    var ignoreAccents: Boolean by mutableStateOf(false)
        private set

    var showFingerColors: Boolean by mutableStateOf(true)
        private set

    var isMenuExpanded: Boolean by mutableStateOf(false)

    var sessionState: TypingSessionState by mutableStateOf(
        TypingEngine.createSession(
            lesson = selectedLesson,
            layout = KeyboardLayouts.byId(selectedLayoutId),
            ignoreAccents = ignoreAccents
        )
    )
        private set

    fun onCharacterTyped(typedChar: Char, nowMillis: Long = currentTimeMillis()) {
        sessionState = TypingEngine.onCharacterTyped(sessionState, typedChar, nowMillis)
    }

    fun onTick(nowMillis: Long = currentTimeMillis()) {
        sessionState = TypingEngine.onTick(sessionState, nowMillis)
    }

    fun selectLayout(layoutId: KeyboardLayoutId) {
        selectedLayoutId = layoutId
        val currentIndex = lessons.indexOfFirst { it.id == selectedLesson.id }.coerceAtLeast(0)
        lessons = LessonRepository.lessonsFor(selectedLanguage, layoutId)
        selectedLesson = lessons.getOrElse(currentIndex) { lessons.first() }
        rebuildSession()
    }

    fun selectLanguage(language: AppLanguage) {
        selectedLanguage = language
        val currentIndex = lessons.indexOfFirst { it.id == selectedLesson.id }.coerceAtLeast(0)
        lessons = LessonRepository.lessonsFor(language, selectedLayoutId)
        selectedLesson = lessons.getOrElse(currentIndex) { lessons.first() }
        rebuildSession()
    }

    fun selectLesson(lesson: TypingLesson) {
        selectedLesson = lesson
        rebuildSession()
    }

    fun toggleIgnoreAccents() {
        ignoreAccents = !ignoreAccents
        sessionState = sessionState.copy(ignoreAccents = ignoreAccents)
    }

    fun toggleFingerColors() {
        showFingerColors = !showFingerColors
    }

    fun resetSession() {
        rebuildSession()
    }

    fun nextLesson() {
        val index = lessons.indexOfFirst { it.id == selectedLesson.id }
        val nextIndex = if (index in 0 until lessons.lastIndex) index + 1 else 0
        selectedLesson = lessons[nextIndex]
        rebuildSession()
    }

    fun redetectKeyboard() {
        val detected = detector.detect()
        detectedInfo = detected
        selectedLayoutId = detected.detectedLayout
        selectedLanguage = detected.detectedLanguage
        lessons = LessonRepository.lessonsFor(selectedLanguage, selectedLayoutId)
        selectedLesson = lessons.first()
        rebuildSession()
    }

    private fun rebuildSession() {
        sessionState = TypingEngine.createSession(
            lesson = selectedLesson,
            layout = KeyboardLayouts.byId(selectedLayoutId),
            ignoreAccents = ignoreAccents
        )
    }
}
