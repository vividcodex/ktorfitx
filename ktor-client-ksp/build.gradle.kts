import com.vanniktech.maven.publish.SonatypeHost

plugins {
	kotlin("jvm") version "1.9.23"
	id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "cn.vividcode.multiplatform.ktor.client.ksp"
version = "2.3.10-1.0.0-Beta1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
	jvmToolchain(21)
}

sourceSets {
	main {
		kotlin.srcDirs("build/generated/ksp/commonMain/kotlin")
		java.srcDirs("build/generated/ksp/commonMain/kotlin")
	}
}

dependencies {
	implementation(projects.ktorClientApi)
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
	implementation("com.google.devtools.ksp:symbol-processing-api:1.9.23-1.0.20")
	implementation("com.google.devtools.ksp:symbol-processing:1.9.23-1.0.20")
	implementation("com.squareup:kotlinpoet:1.16.0")
}

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "ktor-client-ksp", "2.3.10-1.0.0-Beta1")
	
	pom {
		name.set("ktor-client-ksp")
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