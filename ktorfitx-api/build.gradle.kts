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

val ktorfitVersion = property("ktorfitx.version").toString()

group = "cn.vividcode.multiplatform.ktorfitx.api"
version = ktorfitVersion

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
			baseName = "KtorfitApi"
			isStatic = true
		}
	}
	
	sourceSets {
		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(libs.krypto)
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
		minSdk = 24
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
		kotlinCompilerExtensionVersion = property("compose.kotlinCompilerVersion").toString()
	}
}

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "ktorfit", ktorfitVersion)
	
	pom {
		name.set("ktorfit-api")
		description.set("Ktorfit：基于Ktor的网络请求框架，自研的Mock机制，充分利用Kotlin语法特性，提供更好的使用体验是我们的宗旨")
		inceptionYear.set("2024")
		url.set("https://github.com/vividcodex/ktorfit")
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
				url.set("https://github.com/vividcodex/ktorfit")
			}
		}
		
		scm {
			url.set("https://github.com/vividcodex/ktorfit")
			connection.set("scm:git:git://github.com/vividcodex/ktorfitx.git")
			developerConnection.set("scm:git:ssh://git@github.com:vividcodex/ktorfitx.git")
		}
	}
}