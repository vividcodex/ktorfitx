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

kotlin {
	androidTarget {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
	
	jvm("desktop") {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
	
	listOf(
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "SampleApp"
			isStatic = true
		}
		iosTarget.apply {
			compilerOptions {
				apiVersion = KotlinVersion.KOTLIN_2_2
				languageVersion = KotlinVersion.KOTLIN_2_2
			}
		}
	}
	
	js {
		outputModuleName = "sampleApp"
		browser {
			commonWebpackConfig {
				outputFileName = "sampleApp.js"
			}
		}
		binaries.executable()
		useEsModules()
		compilerOptions {
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
	
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
		compilerOptions {
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
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
		androidMain.dependencies {
			implementation(compose.preview)
			implementation(libs.androidx.activity.compose)
		}
		val desktopMain by getting
		desktopMain.dependencies {
			implementation(compose.desktop.currentOs)
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