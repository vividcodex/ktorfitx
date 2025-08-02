plugins {
	`java-gradle-plugin`
	`kotlin-dsl`
	alias(libs.plugins.maven.publish)
}

val ktorfitxVersion = "3.2.3-3.1.0-dev01"
val ktorfitxAutomaticRelease = false

group = "cn.ktorfitx.multiplatform.gradle.plugin"
version = ktorfitxVersion

repositories {
	mavenCentral()
	google()
	gradlePluginPortal()
}

gradlePlugin {
	plugins {
		create("ktorfitxMultiplatformGradlePlugin") {
			id = "cn.ktorfitx.multiplatform"
			displayName = "Kotrfitx Multiplatform Gradle Plugin"
			implementationClass = "cn.ktorfitx.multiplatform.gradle.plugin.KtorfitxMultiplatformPlugin"
		}
	}
}

dependencies {
	implementation(libs.ksp)
	implementation(libs.kotlin.multiplatform)
}

mavenPublishing {
	publishToMavenCentral(automaticRelease = ktorfitxAutomaticRelease)
	signAllPublications()
	
	coordinates("cn.ktorfitx", "multiplatform-gradle-plugin", ktorfitxVersion)
	
	pom {
		name.set("multiplatform-gradle-plugin")
		description.set("Ktorfitx 基于 KSP2 的代码生成框架，在 Kotlin Multiplatform 中是 RESTful API 框架，在 Ktor Server 中是 路由以及参数解析框架")
		inceptionYear.set("2025")
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
			url.set("https://gitlab.com/vividcodex/ktorfitx")
			connection.set("scm:git:git://github.com/vividcodex/ktorfitx.git")
			developerConnection.set("scm:git:ssh://git@github.com:vividcodex/ktorfitx.git")
		}
	}
}