import cn.ktorfitx.build.gradle.getInt
import cn.ktorfitx.common.gradle.plugin.KtorfitxLanguage
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    id("cn.ktorfitx.android")
}

val ktorfitxSampleVersion = property("ktorfitx.sample.version").toString()

android {
    namespace = "cn.ktorfitx.android.sample"
    compileSdk {
        version = release(libs.versions.android.compileSdk.getInt()) {
            minorApiLevel = libs.versions.android.compileSdkMinor.getInt()
        }
    }

    defaultConfig {
        applicationId = "cn.ktorfitx.android.sample"
        minSdk = libs.versions.android.minSdk.getInt()
        targetSdk {
            version = release(libs.versions.android.targetSdk.getInt())
        }
        versionCode = 2
        versionName = ktorfitxSampleVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_4
        languageVersion = KotlinVersion.KOTLIN_2_4
    }
}

dependencies {
    implementation(libs.bundles.android.sample)
    implementation(projects.multiplatformSample)
}

ktorfitx {
    isDevelopmentMode = true
    language = KtorfitxLanguage.CHINESE
    websockets {
        enabled = true
    }
    mock {
        enabled = true
    }
}