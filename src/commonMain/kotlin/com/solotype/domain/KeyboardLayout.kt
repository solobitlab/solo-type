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

enum class KeyboardLayoutId(val displayName: String, val isoCode: String) {
    ABNT2("Português (Brasil) - ABNT2", "pt-BR"),
    US_QWERTY("Inglês (EUA / Internacional) - QWERTY", "en-US"),
    ES_ISO("Espanhol / América Latina - ISO", "es-ES")
}

data class KeyCap(
    val id: String,
    val primaryLabel: String,
    val shiftLabel: String? = null,
    val finger: Finger,
    val widthWeight: Float = 1f,
    val isHomeKeyBump: Boolean = false,
    val isModifier: Boolean = false
)

data class KeyRequirement(
    val primaryKeyId: String,
    val primaryKeyLabel: String,
    val primaryFinger: Finger,
    val requiresShift: Boolean = false,
    val shiftKeyId: String? = null,
    val shiftFinger: Finger? = null
)

data class KeyboardLayout(
    val id: KeyboardLayoutId,
    val rows: List<List<KeyCap>>
) {
    private val allKeys: List<KeyCap> = rows.flatten()
    private val keyById: Map<String, KeyCap> = allKeys.associateBy { it.id }

    fun findKey(id: String): KeyCap? = keyById[id]

    fun resolveKeyRequirement(targetChar: Char): KeyRequirement {
        if (targetChar == ' ') {
            val space = keyById["SPACE"]
            return KeyRequirement(
                primaryKeyId = "SPACE",
                primaryKeyLabel = space?.primaryLabel ?: "Espaço",
                primaryFinger = space?.finger ?: Finger.RIGHT_THUMB
            )
        }
        if (targetChar == '\n' || targetChar == '\r') {
            val enter = keyById["ENTER"]
            return KeyRequirement(
                primaryKeyId = "ENTER",
                primaryKeyLabel = enter?.primaryLabel ?: "Enter",
                primaryFinger = enter?.finger ?: Finger.RIGHT_PINKY
            )
        }
        if (targetChar == '\t') {
            val tab = keyById["TAB"]
            return KeyRequirement(
                primaryKeyId = "TAB",
                primaryKeyLabel = tab?.primaryLabel ?: "Tab",
                primaryFinger = tab?.finger ?: Finger.LEFT_PINKY
            )
        }

        val targetStr = targetChar.toString()

        // 1. Exact shift symbol match (e.g. '!', '@', '?', ':', '<', '>', '"')
        val exactShiftKey = allKeys.firstOrNull {
            !it.isModifier && it.shiftLabel == targetStr
        }
        if (exactShiftKey != null) {
            return buildShiftedRequirement(exactShiftKey)
        }

        // 2. Exact primary character match for non-uppercase symbols/digits
        val exactPrimaryKey = if (!targetChar.isUpperCase()) {
            allKeys.firstOrNull {
                !it.isModifier && it.primaryLabel.length == 1 && it.primaryLabel == targetStr
            }
        } else {
            null
        }
        if (exactPrimaryKey != null) {
            return KeyRequirement(
                primaryKeyId = exactPrimaryKey.id,
                primaryKeyLabel = exactPrimaryKey.primaryLabel,
                primaryFinger = exactPrimaryKey.finger
            )
        }

        // 3. Letter case / diacritic normalization
        val isUpper = targetChar.isUpperCase()
        val normalizedChar = normalizeForLayout(targetChar.lowercaseChar())
        val letterKey = allKeys.firstOrNull {
            !it.isModifier &&
                it.primaryLabel.length == 1 &&
                it.primaryLabel[0].lowercaseChar() == normalizedChar
        }

        if (letterKey != null) {
            return if (isUpper) {
                buildShiftedRequirement(letterKey)
            } else {
                KeyRequirement(
                    primaryKeyId = letterKey.id,
                    primaryKeyLabel = letterKey.primaryLabel,
                    primaryFinger = letterKey.finger
                )
            }
        }

        // Fallback when character does not exist directly on the chosen layout
        return KeyRequirement(
            primaryKeyId = "UNKNOWN_$targetChar",
            primaryKeyLabel = targetStr,
            primaryFinger = Finger.RIGHT_INDEX,
            requiresShift = isUpper,
            shiftKeyId = if (isUpper) "SHIFT_L" else null,
            shiftFinger = if (isUpper) Finger.LEFT_PINKY else null
        )
    }

    private fun buildShiftedRequirement(keyCap: KeyCap): KeyRequirement {
        val shiftFinger = keyCap.finger.oppositeShiftFinger()
        val shiftKeyId = keyCap.finger.oppositeShiftKeyId()
        return KeyRequirement(
            primaryKeyId = keyCap.id,
            primaryKeyLabel = keyCap.primaryLabel,
            primaryFinger = keyCap.finger,
            requiresShift = true,
            shiftKeyId = shiftKeyId,
            shiftFinger = shiftFinger
        )
    }

    private fun normalizeForLayout(ch: Char): Char = when (ch) {
        'á', 'à', 'â', 'ã', 'ä' -> 'a'
        'é', 'è', 'ê', 'ë' -> 'e'
        'í', 'ì', 'î', 'ï' -> 'i'
        'ó', 'ò', 'ô', 'õ', 'ö' -> 'o'
        'ú', 'ù', 'û', 'ü' -> 'u'
        'ç' -> if (allKeys.any { it.primaryLabel.equals("Ç", ignoreCase = true) }) 'ç' else 'c'
        'ñ' -> if (allKeys.any { it.primaryLabel.equals("Ñ", ignoreCase = true) }) 'ñ' else 'n'
        else -> ch
    }
}

object KeyboardLayouts {
    val abnt2: KeyboardLayout by lazy { createAbnt2Layout() }
    val usQwerty: KeyboardLayout by lazy { createUsQwertyLayout() }
    val esIso: KeyboardLayout by lazy { createEsIsoLayout() }

    fun byId(id: KeyboardLayoutId): KeyboardLayout = when (id) {
        KeyboardLayoutId.ABNT2 -> abnt2
        KeyboardLayoutId.US_QWERTY -> usQwerty
        KeyboardLayoutId.ES_ISO -> esIso
    }

    private fun createAbnt2Layout(): KeyboardLayout = KeyboardLayout(
        id = KeyboardLayoutId.ABNT2,
        rows = listOf(
            listOf(
                KeyCap("QUOTE", "'", "\"", Finger.LEFT_PINKY),
                KeyCap("1", "1", "!", Finger.LEFT_PINKY),
                KeyCap("2", "2", "@", Finger.LEFT_RING),
                KeyCap("3", "3", "#", Finger.LEFT_MIDDLE),
                KeyCap("4", "4", "$", Finger.LEFT_INDEX),
                KeyCap("5", "5", "%", Finger.LEFT_INDEX),
                KeyCap("6", "6", "¨", Finger.RIGHT_INDEX),
                KeyCap("7", "7", "&", Finger.RIGHT_INDEX),
                KeyCap("8", "8", "*", Finger.RIGHT_MIDDLE),
                KeyCap("9", "9", "(", Finger.RIGHT_RING),
                KeyCap("0", "0", ")", Finger.RIGHT_PINKY),
                KeyCap("MINUS", "-", "_", Finger.RIGHT_PINKY),
                KeyCap("EQUALS", "=", "+", Finger.RIGHT_PINKY),
                KeyCap("BACKSPACE", "⌫ Back", null, Finger.RIGHT_PINKY, widthWeight = 2.0f, isModifier = true)
            ),
            listOf(
                KeyCap("TAB", "Tab", null, Finger.LEFT_PINKY, widthWeight = 1.75f, isModifier = true),
                KeyCap("Q", "Q", null, Finger.LEFT_PINKY),
                KeyCap("W", "W", null, Finger.LEFT_RING),
                KeyCap("E", "E", null, Finger.LEFT_MIDDLE),
                KeyCap("R", "R", null, Finger.LEFT_INDEX),
                KeyCap("T", "T", null, Finger.LEFT_INDEX),
                KeyCap("Y", "Y", null, Finger.RIGHT_INDEX),
                KeyCap("U", "U", null, Finger.RIGHT_INDEX),
                KeyCap("I", "I", null, Finger.RIGHT_MIDDLE),
                KeyCap("O", "O", null, Finger.RIGHT_RING),
                KeyCap("P", "P", null, Finger.RIGHT_PINKY),
                KeyCap("ACUTE", "´", "`", Finger.RIGHT_PINKY, widthWeight = 1.5f),
                KeyCap("LBRACKET", "[", "{", Finger.RIGHT_PINKY, widthWeight = 1.75f)
            ),
            listOf(
                KeyCap("CAPS", "Caps", null, Finger.LEFT_PINKY, widthWeight = 1.75f, isModifier = true),
                KeyCap("A", "A", null, Finger.LEFT_PINKY),
                KeyCap("S", "S", null, Finger.LEFT_RING),
                KeyCap("D", "D", null, Finger.LEFT_MIDDLE),
                KeyCap("F", "F", null, Finger.LEFT_INDEX, isHomeKeyBump = true),
                KeyCap("G", "G", null, Finger.LEFT_INDEX),
                KeyCap("H", "H", null, Finger.RIGHT_INDEX),
                KeyCap("J", "J", null, Finger.RIGHT_INDEX, isHomeKeyBump = true),
                KeyCap("K", "K", null, Finger.RIGHT_MIDDLE),
                KeyCap("L", "L", null, Finger.RIGHT_RING),
                KeyCap("CEDILLA", "Ç", null, Finger.RIGHT_PINKY),
                KeyCap("TILDE", "~", "^", Finger.RIGHT_PINKY),
                KeyCap("RBRACKET", "]", "}", Finger.RIGHT_PINKY),
                KeyCap("ENTER", "⏎ Enter", null, Finger.RIGHT_PINKY, widthWeight = 1.25f, isModifier = true)
            ),
            listOf(
                KeyCap("SHIFT_L", "⇧ Shift", null, Finger.LEFT_PINKY, widthWeight = 1.5f, isModifier = true),
                KeyCap("BACKSLASH", "\\", "|", Finger.LEFT_PINKY),
                KeyCap("Z", "Z", null, Finger.LEFT_PINKY),
                KeyCap("X", "X", null, Finger.LEFT_RING),
                KeyCap("C", "C", null, Finger.LEFT_MIDDLE),
                KeyCap("V", "V", null, Finger.LEFT_INDEX),
                KeyCap("B", "B", null, Finger.LEFT_INDEX),
                KeyCap("N", "N", null, Finger.RIGHT_INDEX),
                KeyCap("M", "M", null, Finger.RIGHT_INDEX),
                KeyCap("COMMA", ",", "<", Finger.RIGHT_MIDDLE),
                KeyCap("PERIOD", ".", ">", Finger.RIGHT_RING),
                KeyCap("SEMICOLON", ";", ":", Finger.RIGHT_PINKY),
                KeyCap("SLASH", "/", "?", Finger.RIGHT_PINKY),
                KeyCap("SHIFT_R", "⇧ Shift", null, Finger.RIGHT_PINKY, widthWeight = 1.5f, isModifier = true)
            ),
            listOf(
                KeyCap("CTRL_L", "Ctrl", null, Finger.LEFT_PINKY, widthWeight = 1.5f, isModifier = true),
                KeyCap("META_L", "Win/⌘", null, Finger.LEFT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("ALT_L", "Alt", null, Finger.LEFT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("SPACE", "Espaço", null, Finger.RIGHT_THUMB, widthWeight = 7.0f),
                KeyCap("ALT_GR", "AltGr", null, Finger.RIGHT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("MENU", "Fn", null, Finger.RIGHT_PINKY, widthWeight = 1.25f, isModifier = true),
                KeyCap("CTRL_R", "Ctrl", null, Finger.RIGHT_PINKY, widthWeight = 1.5f, isModifier = true)
            )
        )
    )

    private fun createUsQwertyLayout(): KeyboardLayout = KeyboardLayout(
        id = KeyboardLayoutId.US_QWERTY,
        rows = listOf(
            listOf(
                KeyCap("GRAVE", "`", "~", Finger.LEFT_PINKY),
                KeyCap("1", "1", "!", Finger.LEFT_PINKY),
                KeyCap("2", "2", "@", Finger.LEFT_RING),
                KeyCap("3", "3", "#", Finger.LEFT_MIDDLE),
                KeyCap("4", "4", "$", Finger.LEFT_INDEX),
                KeyCap("5", "5", "%", Finger.LEFT_INDEX),
                KeyCap("6", "6", "^", Finger.RIGHT_INDEX),
                KeyCap("7", "7", "&", Finger.RIGHT_INDEX),
                KeyCap("8", "8", "*", Finger.RIGHT_MIDDLE),
                KeyCap("9", "9", "(", Finger.RIGHT_RING),
                KeyCap("0", "0", ")", Finger.RIGHT_PINKY),
                KeyCap("MINUS", "-", "_", Finger.RIGHT_PINKY),
                KeyCap("EQUALS", "=", "+", Finger.RIGHT_PINKY),
                KeyCap("BACKSPACE", "⌫ Back", null, Finger.RIGHT_PINKY, widthWeight = 2.0f, isModifier = true)
            ),
            listOf(
                KeyCap("TAB", "Tab", null, Finger.LEFT_PINKY, widthWeight = 1.5f, isModifier = true),
                KeyCap("Q", "Q", null, Finger.LEFT_PINKY),
                KeyCap("W", "W", null, Finger.LEFT_RING),
                KeyCap("E", "E", null, Finger.LEFT_MIDDLE),
                KeyCap("R", "R", null, Finger.LEFT_INDEX),
                KeyCap("T", "T", null, Finger.LEFT_INDEX),
                KeyCap("Y", "Y", null, Finger.RIGHT_INDEX),
                KeyCap("U", "U", null, Finger.RIGHT_INDEX),
                KeyCap("I", "I", null, Finger.RIGHT_MIDDLE),
                KeyCap("O", "O", null, Finger.RIGHT_RING),
                KeyCap("P", "P", null, Finger.RIGHT_PINKY),
                KeyCap("LBRACKET", "[", "{", Finger.RIGHT_PINKY),
                KeyCap("RBRACKET", "]", "}", Finger.RIGHT_PINKY),
                KeyCap("BACKSLASH", "\\", "|", Finger.RIGHT_PINKY, widthWeight = 1.5f)
            ),
            listOf(
                KeyCap("CAPS", "Caps", null, Finger.LEFT_PINKY, widthWeight = 1.75f, isModifier = true),
                KeyCap("A", "A", null, Finger.LEFT_PINKY),
                KeyCap("S", "S", null, Finger.LEFT_RING),
                KeyCap("D", "D", null, Finger.LEFT_MIDDLE),
                KeyCap("F", "F", null, Finger.LEFT_INDEX, isHomeKeyBump = true),
                KeyCap("G", "G", null, Finger.LEFT_INDEX),
                KeyCap("H", "H", null, Finger.RIGHT_INDEX),
                KeyCap("J", "J", null, Finger.RIGHT_INDEX, isHomeKeyBump = true),
                KeyCap("K", "K", null, Finger.RIGHT_MIDDLE),
                KeyCap("L", "L", null, Finger.RIGHT_RING),
                KeyCap("SEMICOLON", ";", ":", Finger.RIGHT_PINKY),
                KeyCap("QUOTE", "'", "\"", Finger.RIGHT_PINKY),
                KeyCap("ENTER", "⏎ Enter", null, Finger.RIGHT_PINKY, widthWeight = 2.25f, isModifier = true)
            ),
            listOf(
                KeyCap("SHIFT_L", "⇧ Shift", null, Finger.LEFT_PINKY, widthWeight = 2.25f, isModifier = true),
                KeyCap("Z", "Z", null, Finger.LEFT_PINKY),
                KeyCap("X", "X", null, Finger.LEFT_RING),
                KeyCap("C", "C", null, Finger.LEFT_MIDDLE),
                KeyCap("V", "V", null, Finger.LEFT_INDEX),
                KeyCap("B", "B", null, Finger.LEFT_INDEX),
                KeyCap("N", "N", null, Finger.RIGHT_INDEX),
                KeyCap("M", "M", null, Finger.RIGHT_INDEX),
                KeyCap("COMMA", ",", "<", Finger.RIGHT_MIDDLE),
                KeyCap("PERIOD", ".", ">", Finger.RIGHT_RING),
                KeyCap("SLASH", "/", "?", Finger.RIGHT_PINKY),
                KeyCap("SHIFT_R", "⇧ Shift", null, Finger.RIGHT_PINKY, widthWeight = 2.75f, isModifier = true)
            ),
            listOf(
                KeyCap("CTRL_L", "Ctrl", null, Finger.LEFT_PINKY, widthWeight = 1.5f, isModifier = true),
                KeyCap("META_L", "Win/⌘", null, Finger.LEFT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("ALT_L", "Alt", null, Finger.LEFT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("SPACE", "Space", null, Finger.RIGHT_THUMB, widthWeight = 7.0f),
                KeyCap("ALT_R", "Alt", null, Finger.RIGHT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("MENU", "Fn", null, Finger.RIGHT_PINKY, widthWeight = 1.25f, isModifier = true),
                KeyCap("CTRL_R", "Ctrl", null, Finger.RIGHT_PINKY, widthWeight = 1.5f, isModifier = true)
            )
        )
    )

    private fun createEsIsoLayout(): KeyboardLayout = KeyboardLayout(
        id = KeyboardLayoutId.ES_ISO,
        rows = listOf(
            listOf(
                KeyCap("ORDINAL", "º", "ª", Finger.LEFT_PINKY),
                KeyCap("1", "1", "!", Finger.LEFT_PINKY),
                KeyCap("2", "2", "\"", Finger.LEFT_RING),
                KeyCap("3", "3", "·", Finger.LEFT_MIDDLE),
                KeyCap("4", "4", "$", Finger.LEFT_INDEX),
                KeyCap("5", "5", "%", Finger.LEFT_INDEX),
                KeyCap("6", "6", "&", Finger.RIGHT_INDEX),
                KeyCap("7", "7", "/", Finger.RIGHT_INDEX),
                KeyCap("8", "8", "(", Finger.RIGHT_MIDDLE),
                KeyCap("9", "9", ")", Finger.RIGHT_RING),
                KeyCap("0", "0", "=", Finger.RIGHT_PINKY),
                KeyCap("QUOTE", "'", "?", Finger.RIGHT_PINKY),
                KeyCap("INVERTED_EXCL", "¡", "¿", Finger.RIGHT_PINKY),
                KeyCap("BACKSPACE", "⌫ Back", null, Finger.RIGHT_PINKY, widthWeight = 2.0f, isModifier = true)
            ),
            listOf(
                KeyCap("TAB", "Tab", null, Finger.LEFT_PINKY, widthWeight = 1.75f, isModifier = true),
                KeyCap("Q", "Q", null, Finger.LEFT_PINKY),
                KeyCap("W", "W", null, Finger.LEFT_RING),
                KeyCap("E", "E", null, Finger.LEFT_MIDDLE),
                KeyCap("R", "R", null, Finger.LEFT_INDEX),
                KeyCap("T", "T", null, Finger.LEFT_INDEX),
                KeyCap("Y", "Y", null, Finger.RIGHT_INDEX),
                KeyCap("U", "U", null, Finger.RIGHT_INDEX),
                KeyCap("I", "I", null, Finger.RIGHT_MIDDLE),
                KeyCap("O", "O", null, Finger.RIGHT_RING),
                KeyCap("P", "P", null, Finger.RIGHT_PINKY),
                KeyCap("GRAVE", "`", "^", Finger.RIGHT_PINKY, widthWeight = 1.5f),
                KeyCap("PLUS", "+", "*", Finger.RIGHT_PINKY, widthWeight = 1.75f)
            ),
            listOf(
                KeyCap("CAPS", "Caps", null, Finger.LEFT_PINKY, widthWeight = 1.75f, isModifier = true),
                KeyCap("A", "A", null, Finger.LEFT_PINKY),
                KeyCap("S", "S", null, Finger.LEFT_RING),
                KeyCap("D", "D", null, Finger.LEFT_MIDDLE),
                KeyCap("F", "F", null, Finger.LEFT_INDEX, isHomeKeyBump = true),
                KeyCap("G", "G", null, Finger.LEFT_INDEX),
                KeyCap("H", "H", null, Finger.RIGHT_INDEX),
                KeyCap("J", "J", null, Finger.RIGHT_INDEX, isHomeKeyBump = true),
                KeyCap("K", "K", null, Finger.RIGHT_MIDDLE),
                KeyCap("L", "L", null, Finger.RIGHT_RING),
                KeyCap("ENE", "Ñ", null, Finger.RIGHT_PINKY),
                KeyCap("ACUTE", "´", "¨", Finger.RIGHT_PINKY),
                KeyCap("CEDILLA", "Ç", null, Finger.RIGHT_PINKY),
                KeyCap("ENTER", "⏎ Enter", null, Finger.RIGHT_PINKY, widthWeight = 1.25f, isModifier = true)
            ),
            listOf(
                KeyCap("SHIFT_L", "⇧ Shift", null, Finger.LEFT_PINKY, widthWeight = 1.5f, isModifier = true),
                KeyCap("LESS_THAN", "<", ">", Finger.LEFT_PINKY),
                KeyCap("Z", "Z", null, Finger.LEFT_PINKY),
                KeyCap("X", "X", null, Finger.LEFT_RING),
                KeyCap("C", "C", null, Finger.LEFT_MIDDLE),
                KeyCap("V", "V", null, Finger.LEFT_INDEX),
                KeyCap("B", "B", null, Finger.LEFT_INDEX),
                KeyCap("N", "N", null, Finger.RIGHT_INDEX),
                KeyCap("M", "M", null, Finger.RIGHT_INDEX),
                KeyCap("COMMA", ",", ";", Finger.RIGHT_MIDDLE),
                KeyCap("PERIOD", ".", ":", Finger.RIGHT_RING),
                KeyCap("MINUS", "-", "_", Finger.RIGHT_PINKY),
                KeyCap("SHIFT_R", "⇧ Shift", null, Finger.RIGHT_PINKY, widthWeight = 2.5f, isModifier = true)
            ),
            listOf(
                KeyCap("CTRL_L", "Ctrl", null, Finger.LEFT_PINKY, widthWeight = 1.5f, isModifier = true),
                KeyCap("META_L", "Win/⌘", null, Finger.LEFT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("ALT_L", "Alt", null, Finger.LEFT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("SPACE", "Espacio", null, Finger.RIGHT_THUMB, widthWeight = 7.0f),
                KeyCap("ALT_GR", "AltGr", null, Finger.RIGHT_THUMB, widthWeight = 1.25f, isModifier = true),
                KeyCap("MENU", "Fn", null, Finger.RIGHT_PINKY, widthWeight = 1.25f, isModifier = true),
                KeyCap("CTRL_R", "Ctrl", null, Finger.RIGHT_PINKY, widthWeight = 1.5f, isModifier = true)
            )
        )
    )
}
