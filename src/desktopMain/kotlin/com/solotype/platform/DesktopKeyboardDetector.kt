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

package com.solotype.platform

import com.solotype.domain.DetectedKeyboardInfo
import com.solotype.domain.KeyboardEnvironmentDetector
import com.solotype.domain.LayoutInference
import java.awt.im.InputContext
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

class DesktopKeyboardDetector : KeyboardEnvironmentDetector {
    override fun detect(): DetectedKeyboardInfo {
        val osNameRaw = System.getProperty("os.name").orEmpty()
        val osLower = osNameRaw.lowercase()
        val localeCode = detectLocaleCode()

        return when {
            osLower.contains("mac") || osLower.contains("darwin") -> detectMacOs(osNameRaw, localeCode)
            osLower.contains("win") -> detectWindows(osNameRaw, localeCode)
            else -> detectLinux(osNameRaw, localeCode)
        }
    }

    private fun detectLocaleCode(): String {
        val inputLocale = runCatching { InputContext.getInstance()?.locale?.toLanguageTag() }.getOrNull()
            ?.takeIf { it.isNotBlank() && it != "und" }
        val defaultLocale = Locale.getDefault().toLanguageTag()
            .takeIf { it.isNotBlank() && it != "und" }
        val envLang = System.getenv("LANG")
            ?.substringBefore('.')
            ?.replace('_', '-')
            ?.takeIf { it.isNotBlank() }

        return inputLocale ?: defaultLocale ?: envLang ?: "en-US"
    }

    private fun detectMacOs(osName: String, localeCode: String): DetectedKeyboardInfo {
        val plistOutput = runCommand("defaults", "read", "com.apple.HIToolbox", "AppleSelectedInputSources")
        val layoutRegex = Regex(""""KeyboardLayout Name"\s*=\s*"?([^";\n]+)"?""")
        val sourceIdRegex = Regex("""InputSourceID\s*=\s*"?([^";\n]+)"?""")
        val rawLayout = layoutRegex.find(plistOutput)?.groupValues?.getOrNull(1)?.trim()
            ?: sourceIdRegex.find(plistOutput)?.groupValues?.getOrNull(1)?.substringAfterLast('.')?.trim()
            ?: ""

        val ioregOutput = runCommand("ioreg", "-r", "-k", "Product", "-n", "IOHIDKeyboard")
            .ifBlank { runCommand("ioreg", "-r", "-c", "AppleHIDKeyboardEventDriverV2") }
        val productRegex = Regex(""""Product"\s*=\s*"([^"]+)"""")
        val hardwareName = productRegex.find(ioregOutput)?.groupValues?.getOrNull(1)?.trim()
            ?: "Teclado Mac / Padrão"

        return LayoutInference.infer(
            osName = osName,
            hardwareName = hardwareName,
            rawLayout = rawLayout,
            rawLocale = localeCode,
            detectionSource = if (rawLayout.isNotBlank()) "macOS HIToolbox + IOReg" else "macOS Locale / InputContext"
        )
    }

    private fun detectWindows(osName: String, localeCode: String): DetectedKeyboardInfo {
        val regOutput = runCommand("reg", "query", "HKCU\\Keyboard Layout\\Preload", "/v", "1")
        val regRegex = Regex("""1\s+REG_SZ\s+([0-9A-Fa-f]+)""")
        val rawLayout = regRegex.find(regOutput)?.groupValues?.getOrNull(1)?.trim().orEmpty()

        val wmicOutput = runCommand("wmic", "path", "Win32_Keyboard", "get", "Description")
        val wmicName = wmicOutput.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.isNotBlank() && !it.equals("Description", ignoreCase = true) }

        val hardwareName = wmicName ?: run {
            val psOutput = runCommand(
                "powershell",
                "-NoProfile",
                "-Command",
                "(Get-CimInstance Win32_Keyboard | Select-Object -First 1 -ExpandProperty Description)"
            )
            psOutput.lineSequence().map { it.trim() }.firstOrNull { it.isNotBlank() }
        } ?: "Teclado Padrão Windows"

        return LayoutInference.infer(
            osName = osName,
            hardwareName = hardwareName,
            rawLayout = rawLayout,
            rawLocale = localeCode,
            detectionSource = if (rawLayout.isNotBlank()) "Windows Registry + WMI" else "Windows InputContext"
        )
    }

    private fun detectLinux(osName: String, localeCode: String): DetectedKeyboardInfo {
        val xkbFromEtc = runCatching {
            val file = File("/etc/default/keyboard")
            if (!file.exists()) "" else file.readLines()
                .firstOrNull { it.trim().startsWith("XKBLAYOUT=") }
                ?.substringAfter('=')
                ?.trim()
                ?.trim('"', '\'')
                .orEmpty()
        }.getOrDefault("")

        val rawLayout = xkbFromEtc.ifBlank {
            val setxkb = runCommand("setxkbmap", "-query")
            setxkb.lineSequence()
                .firstOrNull { it.trim().startsWith("layout:") }
                ?.substringAfter(':')
                ?.trim()
                ?.substringBefore(',')
                .orEmpty()
        }.ifBlank {
            val localectl = runCommand("localectl", "status")
            localectl.lineSequence()
                .firstOrNull { it.contains("X11 Layout:") }
                ?.substringAfter(':')
                ?.trim()
                ?.substringBefore(',')
                .orEmpty()
        }

        val hardwareName = runCatching {
            File("/sys/class/input")
                .listFiles()
                ?.filter { it.name.startsWith("input") }
                ?.mapNotNull { dir ->
                    val nameFile = File(dir, "name")
                    if (nameFile.exists()) nameFile.readText().trim() else null
                }
                ?.firstOrNull { name ->
                    val lower = name.lowercase()
                    lower.contains("keyboard") || lower.contains("kbd") || lower.contains("keychron") || lower.contains("logitech")
                }
        }.getOrNull() ?: "Teclado Padrão Linux"

        return LayoutInference.infer(
            osName = osName,
            hardwareName = hardwareName,
            rawLayout = rawLayout,
            rawLocale = localeCode,
            detectionSource = if (rawLayout.isNotBlank()) "Linux XKB / sysfs" else "Linux Locale / InputContext"
        )
    }

    private fun runCommand(vararg command: String): String = runCatching {
        val process = ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()
        val finished = process.waitFor(1500, TimeUnit.MILLISECONDS)
        if (!finished) {
            process.destroyForcibly()
            return ""
        }
        process.inputStream.bufferedReader().use { it.readText() }.trim()
    }.getOrDefault("")
}
