import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.android.library)
	alias(libs.plugins.maven.publish)
}

val ktorfitxVersion = property("ktorfitx.version").toString()
val ktorfitxAutomaticRelease = property("ktorfitx.automaticRelease").toString().toBoolean()

group = "cn.vividcode.multiplatform.ktorfitx.api"
version = ktorfitxVersion

kotlin {
	jvmToolchain(21)
	
	androidTarget {
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
		}
	}
	
	jvm("desktop") {
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
		}
	}
	
	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "KtorfitxApi"
			isStatic = true
		}
	}
	
	sourceSets {
		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(libs.krypto)
			implementation(libs.kotlin.reflect)
			api(projects.ktorfitxAnnotation)
			api(libs.ktor.client.cio)
			api(libs.ktor.client.serialization)
			api(libs.ktor.client.content.negotiation)
			api(libs.ktor.serialization.kotlinx.json)
			api(libs.ktor.client.logging)
			api(libs.ktor.client.core)
		}
	}
}

android {
	namespace = "cn.vividcode.multiplatform.ktorfitx.api"
	compileSdk = libs.versions.android.compileSdk.get().toInt()
	
	sourceSets["main"].apply {
		manifest.srcFile("src/androidMain/AndroidManifest.xml")
		res.srcDirs("src/androidMain/res")
		resources.srcDirs("src/commonMain/resources")
	}
	
	defaultConfig {
		minSdk = libs.versions.android.minSdk.get().toInt()
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
			merges += "/META-INF/DEPENDENCIES"
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
	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.compose.kotlin.compiler.get()
	}
}

fun checkVersion() {
	val size = ktorfitxVersion.split("-").size
	check((ktorfitxAutomaticRelease && size == 2) || (!ktorfitxAutomaticRelease && size == 3)) {
		"ktorfitx 的 version 是 $ktorfitxVersion，但是 automaticRelease 是 $ktorfitxAutomaticRelease 的"
	}
}

mavenPublishing {
	checkVersion()
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, ktorfitxAutomaticRelease)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "ktorfitx-api", ktorfitxVersion)
	
	pom {
		name.set("ktorfitx-api")
		description.set("Ktorfitx：基于Ktor的网络请求框架，提供自定义本地Mock，异常处理机制，使用简单")
		inceptionYear.set("2024")
		url.set("https://github.com/vividcodex/ktorfitx")
		licenses {
			license {
				name.set("The Apache License, Version 2.0")
				url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
				distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
			}
		}
		developers {
			developer {
				id.set("li-jia-wei")
				name.set("li-jia-wei")
				url.set("https://github.com/vividcodex/ktorfitx")
			}
		}
		
		scm {
			url.set("https://github.com/vividcodex/ktorfitx")
			connection.set("scm:git:git://github.com/vividcodex/ktorfitx.git")
			developerConnection.set("scm:git:ssh://git@github.com:vividcodex/ktorfitx.git")
		}
	}
}