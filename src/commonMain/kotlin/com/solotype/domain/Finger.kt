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

enum class Hand(val displayNamePt: String) {
    LEFT("Mão Esquerda"),
    RIGHT("Mão Direita")
}

enum class Finger(
    val hand: Hand,
    val displayNamePt: String,
    val shortNamePt: String,
    val fingerIndexOnHand: Int,
    val colorHex: Long
) {
    LEFT_PINKY(Hand.LEFT, "Mindinho Esquerdo", "Mindinho Esq.", 0, 0xFFF43F5E),
    LEFT_RING(Hand.LEFT, "Anelar Esquerdo", "Anelar Esq.", 1, 0xFFF59E0B),
    LEFT_MIDDLE(Hand.LEFT, "Médio Esquerdo", "Médio Esq.", 2, 0xFF10B981),
    LEFT_INDEX(Hand.LEFT, "Indicador Esquerdo", "Indicador Esq.", 3, 0xFF0EA5E9),
    LEFT_THUMB(Hand.LEFT, "Polegar Esquerdo", "Polegar Esq.", 4, 0xFF8B5CF6),
    RIGHT_THUMB(Hand.RIGHT, "Polegar Direito", "Polegar Dir.", 0, 0xFF8B5CF6),
    RIGHT_INDEX(Hand.RIGHT, "Indicador Direito", "Indicador Dir.", 1, 0xFF3B82F6),
    RIGHT_MIDDLE(Hand.RIGHT, "Médio Direito", "Médio Dir.", 2, 0xFF10B981),
    RIGHT_RING(Hand.RIGHT, "Anelar Direito", "Anelar Dir.", 3, 0xFFF59E0B),
    RIGHT_PINKY(Hand.RIGHT, "Mindinho Direito", "Mindinho Dir.", 4, 0xFFF43F5E);

    fun oppositeShiftFinger(): Finger = when (hand) {
        Hand.LEFT -> RIGHT_PINKY
        Hand.RIGHT -> LEFT_PINKY
    }

    fun oppositeShiftKeyId(): String = when (hand) {
        Hand.LEFT -> "SHIFT_R"
        Hand.RIGHT -> "SHIFT_L"
    }
}
