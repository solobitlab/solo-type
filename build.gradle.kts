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

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.compose") version "1.7.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
}

group = "com.solotype"
version = "0.0.1-alpha"

kotlin {
    jvmToolchain(17)
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.solotype.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Deb,
                TargetFormat.Rpm
            )
            packageName = "SoloType"
            packageVersion = "1.0.0"
            description = "SoloType — Treinador de Digitação com os Dedos Corretos"
            vendor = "SoloType"
            copyright = "© 2026 SoloType. All rights reserved."
            modules("java.instrument", "java.management", "jdk.unsupported")

            macOS {
                bundleID = "com.solotype.app"
                dockName = "SoloType"
                appCategory = "public.app-category.education"
                iconFile.set(project.file("packaging/icons/SoloType.icns"))
            }

            windows {
                menuGroup = "SoloType"
                shortcut = true
                dirChooser = true
                perUserInstall = true
                upgradeUuid = "8d3e4f9a-7c1b-4e8a-9b2d-5f6a1c8e9d0b"
                iconFile.set(project.file("packaging/icons/SoloType.ico"))
            }

            linux {
                packageName = "solotype"
                debMaintainer = "SoloType Team <contact@solotype.app>"
                menuGroup = "Education"
                appCategory = "Education"
                shortcut = true
                iconFile.set(project.file("packaging/icons/SoloType.png"))
            }
        }
    }
}
