import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val ktorfitxSampleVersion = property("ktorfitx.sample.version").toString()
val ktorfitxPlatforms = property("ktorfitx.platforms").toString().split(",")

kotlin {
    jvmToolchain(21)

    if ("android" in ktorfitxPlatforms) {
        androidTarget {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_21
            }
        }
    }

    if ("desktop" in ktorfitxPlatforms) {
        jvm("desktop") {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_21
            }
        }
    }

    if ("ios" in ktorfitxPlatforms) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { target ->
            target.binaries.framework {
                baseName = "SampleApp"
                isStatic = true
            }
        }
    }

    if ("macos" in ktorfitxPlatforms) {
        listOf(
            macosX64(),
            macosArm64(),
        ).forEach { target ->
            target.binaries.framework {
                baseName = "SampleApp"
                isStatic = true
            }
        }
    }

    if ("watchos" in ktorfitxPlatforms) {
        listOf(
            watchosX64(),
            watchosArm32(),
            watchosArm64(),
            watchosSimulatorArm64(),
            watchosDeviceArm64(),
        ).forEach { target ->
            target.binaries.framework {
                baseName = "SampleApp"
                isStatic = true
            }
        }
    }

    if ("tvos" in ktorfitxPlatforms) {
        listOf(
            tvosX64(),
            tvosArm64(),
            tvosSimulatorArm64()
        ).forEach { target ->
            target.binaries.framework {
                baseName = "SampleApp"
                isStatic = true
            }
        }
    }

    if ("linux" in ktorfitxPlatforms) {
        listOf(
            linuxArm64(),
            linuxX64()
        ).forEach { target ->
            target.binaries.executable()
        }
    }

    if ("mingw" in ktorfitxPlatforms) {
        mingwX64().binaries.executable()
    }

    if ("js" in ktorfitxPlatforms) {
        js(IR) {
            outputModuleName = "sampleApp"
            browser {
                commonWebpackConfig {
                    outputFileName = "sampleApp.js"
                }
            }
            binaries.executable()
            useEsModules()
        }
    }

    if ("wasmJs" in ktorfitxPlatforms) {
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            outputModuleName = "sampleApp"
            browser {
                val rootDirPath = project.rootDir.path
                val projectDirPath = project.projectDir.path
                commonWebpackConfig {
                    outputFileName = "sampleApp.js"
                    devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static = (static ?: mutableListOf()).apply {
                            add(rootDirPath)
                            add(projectDirPath)
                        }
                    }
                }
            }
            binaries.executable()
            useEsModules()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.multiplatformAnnotation)
                implementation(projects.multiplatformCore)
                implementation(projects.multiplatformMock)
                implementation(projects.multiplatformWebsockets)
                implementation(libs.bundles.multiplatform.sample)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
            }
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        if ("android" in ktorfitxPlatforms) {
            androidMain.dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
            }
        }
        if ("desktop" in ktorfitxPlatforms) {
            val desktopMain by getting
            desktopMain.dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

dependencies {
    kspCommonMainMetadata(projects.multiplatformKsp)
}

tasks.withType<KotlinCompilationTask<*>>().all {
    "kspCommonMainKotlinMetadata".also {
        if (name != it) dependsOn(it)
    }
}

android {
    namespace = "cn.ktorfitx.multiplatform.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "cn.ktorfitx.multiplatform.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = ktorfitxSampleVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
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
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "cn.ktorfitx.multiplatform.sample"
            packageVersion = ktorfitxSampleVersion
        }
    }
}