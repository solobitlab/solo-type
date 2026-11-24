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

import com.solotype.domain.Finger
import com.solotype.domain.KeyboardLayouts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KeyboardLayoutTest {
    @Test
    fun mapsHomeRowKeysAndFingersInAbnt2() {
        val layout = KeyboardLayouts.abnt2

        assertEquals(Finger.LEFT_PINKY, layout.resolveKeyRequirement('a').primaryFinger)
        assertEquals(Finger.LEFT_RING, layout.resolveKeyRequirement('s').primaryFinger)
        assertEquals(Finger.LEFT_MIDDLE, layout.resolveKeyRequirement('d').primaryFinger)
        assertEquals(Finger.LEFT_INDEX, layout.resolveKeyRequirement('f').primaryFinger)
        assertEquals(Finger.RIGHT_INDEX, layout.resolveKeyRequirement('j').primaryFinger)
        assertEquals(Finger.RIGHT_MIDDLE, layout.resolveKeyRequirement('k').primaryFinger)
        assertEquals(Finger.RIGHT_RING, layout.resolveKeyRequirement('l').primaryFinger)
        assertEquals(Finger.RIGHT_PINKY, layout.resolveKeyRequirement('ç').primaryFinger)
        assertFalse(layout.resolveKeyRequirement('f').requiresShift)
    }

    @Test
    fun mapsSpacebarToThumbAndEnterToRightPinky() {
        val layout = KeyboardLayouts.usQwerty

        val spaceReq = layout.resolveKeyRequirement(' ')
        assertEquals("SPACE", spaceReq.primaryKeyId)
        assertEquals(Finger.RIGHT_THUMB, spaceReq.primaryFinger)
        assertFalse(spaceReq.requiresShift)

        val enterReq = layout.resolveKeyRequirement('\n')
        assertEquals("ENTER", enterReq.primaryKeyId)
        assertEquals(Finger.RIGHT_PINKY, enterReq.primaryFinger)
    }

    @Test
    fun mapsShiftedLettersAndSymbolsToOppositeHandShift() {
        val abnt2 = KeyboardLayouts.abnt2

        // 'A' is on the left hand -> requires RIGHT_PINKY Shift
        val upperA = abnt2.resolveKeyRequirement('A')
        assertEquals("A", upperA.primaryKeyId)
        assertEquals(Finger.LEFT_PINKY, upperA.primaryFinger)
        assertTrue(upperA.requiresShift)
        assertEquals("SHIFT_R", upperA.shiftKeyId)
        assertEquals(Finger.RIGHT_PINKY, upperA.shiftFinger)

        // 'Ç' is on the right hand -> requires LEFT_PINKY Shift
        val upperCedilla = abnt2.resolveKeyRequirement('Ç')
        assertEquals("CEDILLA", upperCedilla.primaryKeyId)
        assertEquals(Finger.RIGHT_PINKY, upperCedilla.primaryFinger)
        assertTrue(upperCedilla.requiresShift)
        assertEquals("SHIFT_L", upperCedilla.shiftKeyId)
        assertEquals(Finger.LEFT_PINKY, upperCedilla.shiftFinger)

        // '?' is on the right hand -> requires LEFT_PINKY Shift
        val question = abnt2.resolveKeyRequirement('?')
        assertEquals("SLASH", question.primaryKeyId)
        assertEquals(Finger.RIGHT_PINKY, question.primaryFinger)
        assertTrue(question.requiresShift)
        assertEquals("SHIFT_L", question.shiftKeyId)
        assertEquals(Finger.LEFT_PINKY, question.shiftFinger)

        // '!' is on '1' (left hand) -> requires RIGHT_PINKY Shift
        val bang = abnt2.resolveKeyRequirement('!')
        assertEquals("1", bang.primaryKeyId)
        assertEquals(Finger.LEFT_PINKY, bang.primaryFinger)
        assertTrue(bang.requiresShift)
        assertEquals(Finger.RIGHT_PINKY, bang.shiftFinger)
    }

    @Test
    fun mapsSpanishEneAndInvertedPunctuation() {
        val es = KeyboardLayouts.esIso

        val ene = es.resolveKeyRequirement('ñ')
        assertEquals("ENE", ene.primaryKeyId)
        assertEquals(Finger.RIGHT_PINKY, ene.primaryFinger)
        assertFalse(ene.requiresShift)

        val invertedQuestion = es.resolveKeyRequirement('¿')
        assertEquals("INVERTED_EXCL", invertedQuestion.primaryKeyId)
        assertTrue(invertedQuestion.requiresShift)
        assertEquals(Finger.LEFT_PINKY, invertedQuestion.shiftFinger)
    }
}
