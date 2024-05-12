import com.vanniktech.maven.publish.SonatypeHost

plugins {
	alias(libs.plugins.org.jetbrains.kotlin.jvm)
	alias(libs.plugins.maven.publish)
}

val ktorClientKspVersion: String by project

group = "cn.vividcode.multiplatform.ktor.client.ksp"
version = ktorClientKspVersion

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

kotlin {
	jvmToolchain(17)
}

sourceSets {
	main {
		kotlin.srcDirs("build/generated/ksp/commonMain/kotlin")
		java.srcDirs("build/generated/ksp/commonMain/kotlin")
	}
}

dependencies {
	implementation(projects.ktorClientApi)
	implementation(libs.kotlin.reflect)
	implementation(libs.symbol.processing.api)
	implementation(libs.symbol.processing)
	implementation(libs.kotlinpoet)
}

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "ktor-client-ksp", ktorClientKspVersion)
	
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