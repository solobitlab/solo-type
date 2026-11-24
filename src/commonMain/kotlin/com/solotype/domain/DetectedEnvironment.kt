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

enum class AppLanguage(val code: String, val displayName: String) {
    PT_BR("pt-BR", "Português (Brasil)"),
    EN_US("en-US", "English (US)"),
    ES_ES("es-ES", "Español")
}

data class DetectedKeyboardInfo(
    val osName: String,
    val keyboardHardwareName: String,
    val rawLayoutName: String,
    val rawLocaleCode: String,
    val detectedLayout: KeyboardLayoutId,
    val detectedLanguage: AppLanguage,
    val detectionSource: String
)

interface KeyboardEnvironmentDetector {
    fun detect(): DetectedKeyboardInfo
}

object LayoutInference {
    fun infer(
        osName: String,
        hardwareName: String,
        rawLayout: String,
        rawLocale: String,
        detectionSource: String = "Sistema Operacional"
    ): DetectedKeyboardInfo {
        val layoutLower = rawLayout.lowercase()
        val localeLower = rawLocale.lowercase().replace('-', '_')

        val detectedLayout = inferLayout(layoutLower, localeLower)
        val detectedLanguage = inferLanguage(localeLower, layoutLower)

        return DetectedKeyboardInfo(
            osName = osName.ifBlank { "Desktop" },
            keyboardHardwareName = hardwareName.ifBlank { "Teclado Padrão do Sistema" },
            rawLayoutName = rawLayout.ifBlank { detectedLayout.displayName },
            rawLocaleCode = rawLocale.ifBlank { detectedLanguage.code },
            detectedLayout = detectedLayout,
            detectedLanguage = detectedLanguage,
            detectionSource = detectionSource
        )
    }

    private fun inferLayout(layoutLower: String, localeLower: String): KeyboardLayoutId {
        if (
            layoutLower.contains("abnt") ||
            layoutLower.contains("brazil") ||
            layoutLower.contains("portugues") ||
            layoutLower.contains("portuguese") ||
            layoutLower == "br" ||
            layoutLower.startsWith("br ") ||
            layoutLower.startsWith("br(") ||
            layoutLower.contains("00010416") ||
            layoutLower.contains("00000416") ||
            layoutLower.contains("pt_br") ||
            layoutLower.contains("pt-br")
        ) {
            return KeyboardLayoutId.ABNT2
        }

        if (
            layoutLower.contains("spanish") ||
            layoutLower.contains("espanol") ||
            layoutLower.contains("español") ||
            layoutLower.contains("latam") ||
            layoutLower.contains("latin american") ||
            layoutLower == "es" ||
            layoutLower.startsWith("es ") ||
            layoutLower.startsWith("es(") ||
            layoutLower.contains("0000040a") ||
            layoutLower.contains("0000080a")
        ) {
            return KeyboardLayoutId.ES_ISO
        }

        if (
            layoutLower.contains("u.s.") ||
            layoutLower.contains("us") ||
            layoutLower.contains("english") ||
            layoutLower.contains("qwerty") ||
            layoutLower.contains("international") ||
            layoutLower.contains("00020409") ||
            layoutLower.contains("00000409")
        ) {
            return KeyboardLayoutId.US_QWERTY
        }

        // Fall back to OS locale when raw layout name is absent or ambiguous
        return when {
            localeLower.startsWith("pt") -> KeyboardLayoutId.ABNT2
            localeLower.startsWith("es") -> KeyboardLayoutId.ES_ISO
            else -> KeyboardLayoutId.US_QWERTY
        }
    }

    private fun inferLanguage(localeLower: String, layoutLower: String): AppLanguage {
        return when {
            localeLower.startsWith("pt") -> AppLanguage.PT_BR
            localeLower.startsWith("es") -> AppLanguage.ES_ES
            localeLower.startsWith("en") -> AppLanguage.EN_US
            layoutLower.contains("abnt") || layoutLower.contains("brazil") || layoutLower == "br" -> AppLanguage.PT_BR
            layoutLower.contains("spanish") || layoutLower.contains("latam") || layoutLower == "es" -> AppLanguage.ES_ES
            else -> AppLanguage.EN_US
        }
    }
}
