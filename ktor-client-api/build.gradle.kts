import com.vanniktech.maven.publish.SonatypeHost

plugins {
	kotlin("multiplatform") version "1.9.23"
	kotlin("plugin.serialization") version "1.9.23"
	id("com.android.library") version "8.2.2"
	id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "cn.vividcode.multiplatform.ktor.client.api"
version = "2.3.10-1.0.0-Beta2"

kotlin {
	androidTarget {
		compilations.all {
			kotlinOptions {
				jvmTarget = "21"
			}
		}
	}
	jvm("desktop") {
		compilations.all {
			kotlinOptions {
				jvmTarget = "21"
			}
		}
	}
	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach {
		it.binaries.framework {
			baseName = "VividCodeKtorClientApi"
			isStatic = true
		}
	}
	sourceSets {
		commonMain.dependencies {
			api("io.ktor:ktor-client-core:2.3.10")
			api("io.ktor:ktor-client-logging:2.3.10")
			api("io.ktor:ktor-client-cio:2.3.10")
			api("io.ktor:ktor-client-serialization:2.3.10")
			api("io.ktor:ktor-client-content-negotiation:2.3.10")
			api("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
			implementation("com.soywiz.korlibs.krypto:krypto:4.0.10")
		}
	}
}

android {
	namespace = "cn.vividcode.multiplatform.ktor.client.api"
	compileSdk = 34
	
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
		kotlinCompilerExtensionVersion = "1.5.13"
	}
}

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "ktor-client-api", version.toString())
	
	pom {
		name.set("ktor-client-api")
		description.set("这是一个 Kotlin Multiplatform 用于生成 Ktor 网络请求代码的框架")
		inceptionYear.set("2024")
		url.set("https://gitlab.com/vividcode/multiplatform-ktor-client")
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
				url.set("https://gitlab.com/vividcode/multiplatform-ktor-client")
			}
		}
		
		scm {
			url.set("https://gitlab.com/vividcode/multiplatform-ktor-client")
			connection.set("scm:git:git://gitlab.com/vividcode/multiplatform-ktor-client.git")
			developerConnection.set("scm:git:ssh://git@gitlab.com:vividcode/multiplatform-ktor-client.git")
		}
	}
}