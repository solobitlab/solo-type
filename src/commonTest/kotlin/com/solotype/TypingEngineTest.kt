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

package com.solotype

import com.solotype.domain.AppLanguage
import com.solotype.domain.CharState
import com.solotype.domain.Finger
import com.solotype.domain.KeyboardLayouts
import com.solotype.domain.LessonCategory
import com.solotype.domain.TypingEngine
import com.solotype.domain.TypingLesson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TypingEngineTest {
    private val sampleLesson = TypingLesson(
        id = "test",
        language = AppLanguage.PT_BR,
        category = LessonCategory.HOME_ROW,
        title = "Test",
        description = "Test",
        text = "fá j"
    )

    @Test
    fun enforcesExactOrderAndTracksErrorsAndCorrections() {
        val layout = KeyboardLayouts.abnt2
        var state = TypingEngine.createSession(sampleLesson, layout)

        assertEquals(0, state.currentIndex)
        assertEquals(CharState.CURRENT, state.charStates[0])
        assertEquals('f', state.currentTargetChar)

        // Wrong key pressed for 'f' -> stays at index 0
        state = TypingEngine.onCharacterTyped(state, 'g', 1_000L)
        assertEquals(0, state.currentIndex)
        assertEquals(CharState.CURRENT_ERROR, state.charStates[0])
        assertEquals("G", state.lastWrongKeyPressed)
        assertEquals(1, state.errorKeystrokes)
        assertEquals(0, state.correctKeystrokes)
        assertEquals(0, state.currentStreak)
        assertEquals(1, state.fingerStats[Finger.LEFT_INDEX]?.misses)

        // Correct key pressed after error -> marks CORRECTED and advances to index 1
        state = TypingEngine.onCharacterTyped(state, 'f', 1_500L)
        assertEquals(1, state.currentIndex)
        assertEquals(CharState.CORRECTED, state.charStates[0])
        assertEquals(CharState.CURRENT, state.charStates[1])
        assertNull(state.lastWrongKeyPressed)
        assertEquals(1, state.correctKeystrokes)
        assertEquals(1, state.currentStreak)
        assertEquals(1, state.fingerStats[Finger.LEFT_INDEX]?.hits)
        assertEquals(1, state.fingerStats[Finger.LEFT_INDEX]?.misses)
        assertEquals(50.0, state.accuracyPercent, 0.001)

        // Control characters are ignored
        val beforeCtrl = state
        state = TypingEngine.onCharacterTyped(state, '\u0008', 1_700L)
        assertEquals(beforeCtrl, state)
    }

    @Test
    fun calculatesWpmCpmAccuracyAndCompletion() {
        val lesson = sampleLesson.copy(text = "asdf jklç ")
        val layout = KeyboardLayouts.abnt2
        var state = TypingEngine.createSession(lesson, layout)

        var time = 10_000L
        lesson.text.forEach { ch ->
            state = TypingEngine.onCharacterTyped(state, ch, time)
            time += 600L
        }

        // 10 chars over 5400 ms (9 intervals * 600 ms) -> elapsed = 5400 ms
        assertTrue(state.isCompleted)
        assertEquals(10, state.correctKeystrokes)
        assertEquals(0, state.errorKeystrokes)
        assertEquals(100.0, state.accuracyPercent, 0.001)
        assertEquals(10, state.bestStreak)
        assertNull(state.currentTargetChar)
        assertNull(state.currentRequirement)

        // WPM = (10 / 5) / (5400 / 60000) = 2 / 0.09 = 22.22...
        assertEquals(22.222, state.wpm, 0.05)
        // CPM = 10 / 0.09 = 111.11...
        assertEquals(111.111, state.cpm, 0.05)

        // Further input after completion is ignored
        val completedState = state
        state = TypingEngine.onCharacterTyped(state, 'a', time + 1_000L)
        assertEquals(completedState, state)
    }

    @Test
    fun supportsOptionalSimplifiedAccentsToggle() {
        val layout = KeyboardLayouts.abnt2
        val strictState = TypingEngine.createSession(sampleLesson, layout, ignoreAccents = false)
        val afterFirst = TypingEngine.onCharacterTyped(strictState, 'f', 1_000L)
        val strictMiss = TypingEngine.onCharacterTyped(afterFirst, 'a', 1_200L)
        assertEquals(1, strictMiss.currentIndex)
        assertEquals(1, strictMiss.errorKeystrokes)

        val relaxedState = TypingEngine.createSession(sampleLesson, layout, ignoreAccents = true)
        val relaxedFirst = TypingEngine.onCharacterTyped(relaxedState, 'f', 1_000L)
        val relaxedHit = TypingEngine.onCharacterTyped(relaxedFirst, 'a', 1_200L)
        assertEquals(2, relaxedHit.currentIndex)
        assertEquals(0, relaxedHit.errorKeystrokes)
        assertFalse(relaxedHit.isCompleted)
    }
}
