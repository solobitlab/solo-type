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

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.solotype.domain.AppVersion
import com.solotype.platform.DesktopKeyboardDetector
import com.solotype.ui.SoloTypeApp
import com.solotype.ui.SoloTypeController
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent as AwtKeyEvent

fun main() = application {
    val controller = remember { SoloTypeController(DesktopKeyboardDetector()) }
    val windowState = rememberWindowState(size = DpSize(1220.dp, 880.dp))

    Window(
        onCloseRequest = ::exitApplication,
        title = "SoloType ${AppVersion.VERSION_NAME} — Treinador de Digitação",
        state = windowState
    ) {
        DisposableEffect(window, controller) {
            val focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
            val dispatcher = KeyEventDispatcher { awtEvent ->
                if (!window.isFocused || controller.isMenuExpanded) {
                    return@KeyEventDispatcher false
                }

                if (awtEvent.id == AwtKeyEvent.KEY_PRESSED && awtEvent.keyCode == AwtKeyEvent.VK_ESCAPE) {
                    controller.resetSession()
                    return@KeyEventDispatcher true
                }

                if (awtEvent.id == AwtKeyEvent.KEY_TYPED) {
                    val isPureControlShortcut =
                        awtEvent.isMetaDown || (awtEvent.isControlDown && !awtEvent.isAltDown)
                    if (isPureControlShortcut) {
                        return@KeyEventDispatcher false
                    }

                    val typedChar = awtEvent.keyChar
                    if (typedChar != AwtKeyEvent.CHAR_UNDEFINED) {
                        controller.onCharacterTyped(typedChar, System.currentTimeMillis())
                        return@KeyEventDispatcher true
                    }
                }

                false
            }

            focusManager.addKeyEventDispatcher(dispatcher)
            onDispose {
                focusManager.removeKeyEventDispatcher(dispatcher)
            }
        }

        SoloTypeApp(controller = controller)
    }
}
