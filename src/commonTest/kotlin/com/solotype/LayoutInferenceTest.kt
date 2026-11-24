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
import com.solotype.domain.AppVersion
import com.solotype.domain.KeyboardLayoutId
import com.solotype.domain.LayoutInference
import kotlin.test.Test
import kotlin.test.assertEquals

class LayoutInferenceTest {
    @Test
    fun infersAbnt2AndPtBrOnMacOs() {
        val result = LayoutInference.infer(
            osName = "Mac OS X",
            hardwareName = "Apple Internal Keyboard / Trackpad",
            rawLayout = "Brazilian - ABNT2",
            rawLocale = "pt-BR"
        )
        assertEquals(KeyboardLayoutId.ABNT2, result.detectedLayout)
        assertEquals(AppLanguage.PT_BR, result.detectedLanguage)
        assertEquals("Apple Internal Keyboard / Trackpad", result.keyboardHardwareName)
    }

    @Test
    fun infersUsQwertyAndEnUsOnWindows() {
        val result = LayoutInference.infer(
            osName = "Windows 11",
            hardwareName = "HID Keyboard Device",
            rawLayout = "00020409",
            rawLocale = "en-US"
        )
        assertEquals(KeyboardLayoutId.US_QWERTY, result.detectedLayout)
        assertEquals(AppLanguage.EN_US, result.detectedLanguage)
    }

    @Test
    fun infersAbnt2FromWindowsRegistryCode() {
        val result = LayoutInference.infer(
            osName = "Windows 10",
            hardwareName = "USB Keyboard",
            rawLayout = "00010416",
            rawLocale = "pt_BR"
        )
        assertEquals(KeyboardLayoutId.ABNT2, result.detectedLayout)
        assertEquals(AppLanguage.PT_BR, result.detectedLanguage)
    }

    @Test
    fun infersEsIsoAndSpanishOnLinux() {
        val result = LayoutInference.infer(
            osName = "Linux",
            hardwareName = "AT Translated Set 2 keyboard",
            rawLayout = "es",
            rawLocale = "es_ES"
        )
        assertEquals(KeyboardLayoutId.ES_ISO, result.detectedLayout)
        assertEquals(AppLanguage.ES_ES, result.detectedLanguage)
    }

    @Test
    fun fallsBackToLocaleWhenRawLayoutIsBlank() {
        val result = LayoutInference.infer(
            osName = "Linux",
            hardwareName = "",
            rawLayout = "",
            rawLocale = "pt_BR"
        )
        assertEquals(KeyboardLayoutId.ABNT2, result.detectedLayout)
        assertEquals(AppLanguage.PT_BR, result.detectedLanguage)
        assertEquals("Teclado Padrão do Sistema", result.keyboardHardwareName)
    }

    @Test
    fun exposesCurrentAppVersion() {
        assertEquals("v0.0.1-alpha", AppVersion.VERSION_NAME)
        assertEquals("0.0.1-alpha", AppVersion.RAW_VERSION)
    }
}
