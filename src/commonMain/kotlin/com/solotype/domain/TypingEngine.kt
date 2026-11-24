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

package com.solotype.domain

enum class CharState {
    PENDING,
    CURRENT,
    CORRECT_FIRST_TRY,
    CORRECTED,
    CURRENT_ERROR
}

data class FingerStats(
    val hits: Int = 0,
    val misses: Int = 0
) {
    val total: Int get() = hits + misses
    val accuracyPercent: Double
        get() = if (total == 0) 100.0 else (hits.toDouble() / total.toDouble()) * 100.0
}

data class TypingSessionState(
    val lesson: TypingLesson,
    val layout: KeyboardLayout,
    val ignoreAccents: Boolean = false,
    val currentIndex: Int = 0,
    val charStates: List<CharState> = emptyList(),
    val startTimeMillis: Long? = null,
    val elapsedMillis: Long = 0L,
    val totalKeystrokes: Int = 0,
    val correctKeystrokes: Int = 0,
    val errorKeystrokes: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastWrongKeyPressed: String? = null,
    val lastExpectedRequirement: KeyRequirement? = null,
    val fingerStats: Map<Finger, FingerStats> = emptyMap(),
    val isCompleted: Boolean = false
) {
    val currentTargetChar: Char?
        get() = if (isCompleted) null else lesson.text.getOrNull(currentIndex)

    val currentRequirement: KeyRequirement?
        get() = currentTargetChar?.let { layout.resolveKeyRequirement(it) }

    val wpm: Double
        get() = if (elapsedMillis < 500L) 0.0 else (correctKeystrokes / 5.0) / (elapsedMillis / 60_000.0)

    val cpm: Double
        get() = if (elapsedMillis < 500L) 0.0 else correctKeystrokes / (elapsedMillis / 60_000.0)

    val accuracyPercent: Double
        get() = if (totalKeystrokes == 0) 100.0 else (correctKeystrokes.toDouble() / totalKeystrokes.toDouble()) * 100.0

    val progressPercent: Float
        get() = if (lesson.text.isEmpty()) 1f else currentIndex.toFloat() / lesson.text.length.toFloat()
}

object TypingEngine {
    fun createSession(
        lesson: TypingLesson,
        layout: KeyboardLayout,
        ignoreAccents: Boolean = false
    ): TypingSessionState {
        val text = lesson.text
        val states = List(text.length) { index ->
            if (index == 0) CharState.CURRENT else CharState.PENDING
        }
        return TypingSessionState(
            lesson = lesson,
            layout = layout,
            ignoreAccents = ignoreAccents,
            currentIndex = 0,
            charStates = states,
            isCompleted = text.isEmpty()
        )
    }

    fun onCharacterTyped(
        state: TypingSessionState,
        typedChar: Char,
        nowMillis: Long
    ): TypingSessionState {
        if (state.isCompleted) return state
        val normalizedTyped = if (typedChar == '\r') '\n' else typedChar
        if (normalizedTyped.code < 32 && normalizedTyped != '\n' && normalizedTyped != '\t') {
            return state
        }

        val targetChar = state.currentTargetChar ?: return state
        val requirement = state.layout.resolveKeyRequirement(targetChar)
        val startTime = state.startTimeMillis ?: nowMillis
        val elapsed = (nowMillis - startTime).coerceAtLeast(state.elapsedMillis)

        val isMatch = charactersMatch(
            typed = normalizedTyped,
            target = targetChar,
            ignoreAccents = state.ignoreAccents
        )

        val updatedStates = state.charStates.toMutableList()
        val updatedFingerStats = state.fingerStats.toMutableMap()
        val currentFingerStat = updatedFingerStats[requirement.primaryFinger] ?: FingerStats()

        return if (isMatch) {
            val wasError = updatedStates[state.currentIndex] == CharState.CURRENT_ERROR
            updatedStates[state.currentIndex] = if (wasError) {
                CharState.CORRECTED
            } else {
                CharState.CORRECT_FIRST_TRY
            }

            val nextIndex = state.currentIndex + 1
            val completed = nextIndex >= state.lesson.text.length
            if (!completed) {
                updatedStates[nextIndex] = CharState.CURRENT
            }

            updatedFingerStats[requirement.primaryFinger] = currentFingerStat.copy(
                hits = currentFingerStat.hits + 1
            )

            val newStreak = state.currentStreak + 1
            state.copy(
                currentIndex = nextIndex,
                charStates = updatedStates,
                startTimeMillis = startTime,
                elapsedMillis = elapsed,
                totalKeystrokes = state.totalKeystrokes + 1,
                correctKeystrokes = state.correctKeystrokes + 1,
                currentStreak = newStreak,
                bestStreak = maxOf(state.bestStreak, newStreak),
                lastWrongKeyPressed = null,
                lastExpectedRequirement = null,
                fingerStats = updatedFingerStats,
                isCompleted = completed
            )
        } else {
            updatedStates[state.currentIndex] = CharState.CURRENT_ERROR
            updatedFingerStats[requirement.primaryFinger] = currentFingerStat.copy(
                misses = currentFingerStat.misses + 1
            )
            val wrongKeyId = state.layout.resolveKeyRequirement(normalizedTyped).primaryKeyId

            state.copy(
                charStates = updatedStates,
                startTimeMillis = startTime,
                elapsedMillis = elapsed,
                totalKeystrokes = state.totalKeystrokes + 1,
                errorKeystrokes = state.errorKeystrokes + 1,
                currentStreak = 0,
                lastWrongKeyPressed = wrongKeyId,
                lastExpectedRequirement = requirement,
                fingerStats = updatedFingerStats
            )
        }
    }

    fun onTick(state: TypingSessionState, nowMillis: Long): TypingSessionState {
        val start = state.startTimeMillis ?: return state
        if (state.isCompleted) return state
        val elapsed = (nowMillis - start).coerceAtLeast(state.elapsedMillis)
        return if (elapsed == state.elapsedMillis) state else state.copy(elapsedMillis = elapsed)
    }

    private fun charactersMatch(typed: Char, target: Char, ignoreAccents: Boolean): Boolean {
        if (typed == target) return true
        if (!ignoreAccents) return false
        return stripDiacritic(typed) == stripDiacritic(target)
    }

    private fun stripDiacritic(ch: Char): Char = when (ch) {
        'á', 'à', 'â', 'ã', 'ä' -> 'a'
        'Á', 'À', 'Â', 'Ã', 'Ä' -> 'A'
        'é', 'è', 'ê', 'ë' -> 'e'
        'É', 'È', 'Ê', 'Ë' -> 'E'
        'í', 'ì', 'î', 'ï' -> 'i'
        'Í', 'Ì', 'Î', 'Ï' -> 'I'
        'ó', 'ò', 'ô', 'õ', 'ö' -> 'o'
        'Ó', 'Ò', 'Ô', 'Õ', 'Ö' -> 'O'
        'ú', 'ù', 'û', 'ü' -> 'u'
        'Ú', 'Ù', 'Û', 'Ü' -> 'U'
        'ç' -> 'c'
        'Ç' -> 'C'
        'ñ' -> 'n'
        'Ñ' -> 'N'
        else -> ch
    }
}
