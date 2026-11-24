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

enum class LessonCategory(val titlePt: String) {
    HOME_ROW("1. Fila Base (ASDF JKLÇ)"),
    TOP_ROW("2. Fila Superior (QWERTY UIOP)"),
    BOTTOM_ROW("3. Fila Inferior (ZXCV BNM)"),
    WORDS("4. Palavras Comuns"),
    SENTENCES("5. Frases Fluidas"),
    ACCENTS_AND_PUNCTUATION("6. Acentos e Pontuação"),
    NUMBERS_AND_SYMBOLS("7. Números e Código")
}

data class TypingLesson(
    val id: String,
    val language: AppLanguage,
    val category: LessonCategory,
    val title: String,
    val description: String,
    val text: String
)

object LessonRepository {
    fun lessonsFor(language: AppLanguage, layoutId: KeyboardLayoutId): List<TypingLesson> {
        val homeRightPinky = when (layoutId) {
            KeyboardLayoutId.ABNT2 -> "ç"
            KeyboardLayoutId.US_QWERTY -> ";"
            KeyboardLayoutId.ES_ISO -> "ñ"
        }

        return when (language) {
            AppLanguage.PT_BR -> listOf(
                TypingLesson(
                    id = "pt_home",
                    language = AppLanguage.PT_BR,
                    category = LessonCategory.HOME_ROW,
                    title = "Fila Base: Posição Inicial",
                    description = "Mantenha os indicadores sobre F e J. Use cada dedo na sua coluna.",
                    text = "asdf jkl$homeRightPinky asdf jkl$homeRightPinky fjdk sl a$homeRightPinky gh fjdk sl a$homeRightPinky"
                ),
                TypingLesson(
                    id = "pt_top",
                    language = AppLanguage.PT_BR,
                    category = LessonCategory.TOP_ROW,
                    title = "Fila Superior: Alcance Leve",
                    description = "Suba apenas o dedo necessário e retorne para a fila base.",
                    text = "qwer uiop qwer uiop tre yui wer oip qwer tyui op"
                ),
                TypingLesson(
                    id = "pt_bottom",
                    language = AppLanguage.PT_BR,
                    category = LessonCategory.BOTTOM_ROW,
                    title = "Fila Inferior: Precisão",
                    description = "Desça os dedos sem girar o pulso. Atenção ao polegar no Espaço.",
                    text = "zxcv bnm, zxcv bnm. cvbn m,xz vbnm zxcv bnm,."
                ),
                TypingLesson(
                    id = "pt_words",
                    language = AppLanguage.PT_BR,
                    category = LessonCategory.WORDS,
                    title = "Palavras do Dia a Dia",
                    description = "Ganhe ritmo com palavras curtas e frequentes em português.",
                    text = "casa dado fala gato hora janela livro mesa noite pedra quadro rio sol tempo vento"
                ),
                TypingLesson(
                    id = "pt_sentences",
                    language = AppLanguage.PT_BR,
                    category = LessonCategory.SENTENCES,
                    title = "Frases Fluidas com Maiúsculas",
                    description = "Use o Shift da mão oposta para letras maiúsculas.",
                    text = "O treino constante traz velocidade e precisao. Digite cada tecla na ordem certa com calma."
                ),
                TypingLesson(
                    id = "pt_accents",
                    language = AppLanguage.PT_BR,
                    category = LessonCategory.ACCENTS_AND_PUNCTUATION,
                    title = "Acentos, Cedilha e Pontuação",
                    description = "Pratique acentuação, cedilha e pontuação natural da língua portuguesa.",
                    text = "Atenção! Você já domina a digitação? Coração, ação, lição e precisão são essenciais."
                ),
                TypingLesson(
                    id = "pt_code",
                    language = AppLanguage.PT_BR,
                    category = LessonCategory.NUMBERS_AND_SYMBOLS,
                    title = "Números e Símbolos",
                    description = "Combine a linha numérica e símbolos usados no trabalho e programação.",
                    text = "val total = 100 + 250; if (taxa >= 95) { return true; } // 2026-01"
                )
            )

            AppLanguage.EN_US -> listOf(
                TypingLesson(
                    id = "en_home",
                    language = AppLanguage.EN_US,
                    category = LessonCategory.HOME_ROW,
                    title = "Home Row Foundation",
                    description = "Rest index fingers on F and J bumps and return after every stroke.",
                    text = "asdf jkl$homeRightPinky asdf jkl$homeRightPinky fjdk sla$homeRightPinky gh fjdk sla$homeRightPinky"
                ),
                TypingLesson(
                    id = "en_top",
                    language = AppLanguage.EN_US,
                    category = LessonCategory.TOP_ROW,
                    title = "Top Row Reach",
                    description = "Reach upward smoothly with the aligned finger.",
                    text = "qwer uiop qwer uiop true your type write quiet power"
                ),
                TypingLesson(
                    id = "en_bottom",
                    language = AppLanguage.EN_US,
                    category = LessonCategory.BOTTOM_ROW,
                    title = "Bottom Row Control",
                    description = "Move fingers downward cleanly while keeping wrists relaxed.",
                    text = "zxcv bnm, zxcv bnm. calm zoom next move cave box"
                ),
                TypingLesson(
                    id = "en_words",
                    language = AppLanguage.EN_US,
                    category = LessonCategory.WORDS,
                    title = "Common English Words",
                    description = "Build rhythm on high-frequency words.",
                    text = "about after clear focus great light never quick rhythm solid train world"
                ),
                TypingLesson(
                    id = "en_sentences",
                    language = AppLanguage.EN_US,
                    category = LessonCategory.SENTENCES,
                    title = "Fluent Sentences & Shift",
                    description = "Use opposite-hand Shift for uppercase letters.",
                    text = "The quick brown fox jumps over the lazy dog. Accuracy always builds real speed."
                ),
                TypingLesson(
                    id = "en_punct",
                    language = AppLanguage.EN_US,
                    category = LessonCategory.ACCENTS_AND_PUNCTUATION,
                    title = "Punctuation & Dialogue",
                    description = "Practice commas, periods, question marks, and quotes.",
                    text = "Ready to type faster? Yes! Keep your eyes on the text, stay steady, and avoid rushing."
                ),
                TypingLesson(
                    id = "en_code",
                    language = AppLanguage.EN_US,
                    category = LessonCategory.NUMBERS_AND_SYMBOLS,
                    title = "Numbers & Code Symbols",
                    description = "Develop muscle memory for digits and programming punctuation.",
                    text = "fun score(hits: Int = 42): Boolean = hits > 30 && hits != 99"
                )
            )

            AppLanguage.ES_ES -> listOf(
                TypingLesson(
                    id = "es_home",
                    language = AppLanguage.ES_ES,
                    category = LessonCategory.HOME_ROW,
                    title = "Fila Base: Posición Inicial",
                    description = "Coloca los dedos índices sobre F y J y regresa después de cada tecla.",
                    text = "asdf jkl$homeRightPinky asdf jkl$homeRightPinky fjdk sla$homeRightPinky gh fjdk sla$homeRightPinky"
                ),
                TypingLesson(
                    id = "es_top",
                    language = AppLanguage.ES_ES,
                    category = LessonCategory.TOP_ROW,
                    title = "Fila Superior",
                    description = "Alcanza cada tecla superior con su dedo correspondiente.",
                    text = "qwer uiop qwer uiop quiero tiempo puerto juego"
                ),
                TypingLesson(
                    id = "es_bottom",
                    language = AppLanguage.ES_ES,
                    category = LessonCategory.BOTTOM_ROW,
                    title = "Fila Inferior",
                    description = "Mantén las muñecas firmes y baja solo el dedo necesario.",
                    text = "zxcv bnm, zxcv bnm. voz caza nube mano bien"
                ),
                TypingLesson(
                    id = "es_words",
                    language = AppLanguage.ES_ES,
                    category = LessonCategory.WORDS,
                    title = "Palabras Frecuentes",
                    description = "Mejora tu cadencia con palabras comunes en español.",
                    text = "agua arbol camino diario escribir fuerza gente libro mundo noche orden tiempo"
                ),
                TypingLesson(
                    id = "es_sentences",
                    language = AppLanguage.ES_ES,
                    category = LessonCategory.SENTENCES,
                    title = "Frases Fluidas",
                    description = "Usa el Shift de la mano contraria para las mayúsculas.",
                    text = "La practica diaria mejora la velocidad y evita errores al escribir textos largos."
                ),
                TypingLesson(
                    id = "es_accents",
                    language = AppLanguage.ES_ES,
                    category = LessonCategory.ACCENTS_AND_PUNCTUATION,
                    title = "Tildes, Ñ y Puntuación",
                    description = "Entrena palabras con tilde, letra ñ y signos de puntuación.",
                    text = "Mañana será un día increíble! Cómo está tu precisión hoy? Atención y ritmo."
                ),
                TypingLesson(
                    id = "es_code",
                    language = AppLanguage.ES_ES,
                    category = LessonCategory.NUMBERS_AND_SYMBOLS,
                    title = "Números y Símbolos",
                    description = "Practica números y símbolos técnicos con todos los dedos.",
                    text = "let total = 150 + 350; if (aciertos >= 95) { return true; } // 2026"
                )
            )
        }
    }
}
