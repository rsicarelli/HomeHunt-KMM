@file:Suppress("UNUSED_VARIABLE")

import Application.Android
import Application.iOS
import Application.iOS.CocoaPods
import SQLDelight.Database
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin(KotlinPlugins.multiplatform)
    kotlin(KotlinPlugins.cocoapods)
    kotlin(KotlinPlugins.serialization) version Kotlin.version
    id(Plugins.apollo).version(Apollo.version)
    id(Plugins.androidLibrary)
    id(Plugins.sqlDelight)
}

group = Application.group
version = Application.version

android {
    compileSdk = Android.compileSdk
    sourceSets[Application.srcSet].manifest.srcFile(Android.manifestPath)
    defaultConfig {
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // workaround for https://youtrack.jetbrains.com/issue/KT-43944
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}

kotlin {
    android()

    with(iOS) {
        val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
            System.getenv("SDK_NAME")?.startsWith(iPhoneOS) == true -> ::iosArm64
            System.getenv("NATIVE_ARCH")?.startsWith(arm) == true -> ::iosSimulatorArm64
            else -> ::iosX64
        }

        iosTarget(ios) {}

        cocoapods {
            summary = CocoaPods.summary
            homepage = CocoaPods.homepage
            ios.deploymentTarget = CocoaPods.deploymentTarget
            podfile = project.file(CocoaPods.podFilePath)
            framework { baseName = CocoaPods.frameworkName }
        }

    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            dependencies {
                api(Apollo.runtime)
                implementation(Kotlin.coroutinesCore)
                implementation(Dependencies.multiplatformSettings)
                implementation(SQLDelight.runtime)
                implementation(SQLDelight.coroutinesExtensions)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(SQLDelight.androidDriver)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(SQLDelight.nativeDriver)
            }
        }
        val iosTest by getting
    }
}

apollo { generateKotlinModels.set(true) }

sqldelight {
    database(Database.name) {
        packageName = Database.packageName
        sourceFolders = listOf(Database.sourceFolder)
    }
}
