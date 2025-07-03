import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	alias(libs.plugins.org.jetbrains.kotlin.jvm)
	alias(libs.plugins.maven.publish)
}

val ktorfitxVersion = property("ktorfitx.version").toString()
val ktorfitxAutomaticRelease = property("ktorfitx.automaticRelease").toString().toBoolean()

group = "cn.ktorfitx.server.annotation"
version = ktorfitxVersion

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
	jvmToolchain(21)
	
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_21)
		apiVersion = KotlinVersion.KOTLIN_2_2
		languageVersion = KotlinVersion.KOTLIN_2_2
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
	publishToMavenCentral(automaticRelease = ktorfitxAutomaticRelease)
	signAllPublications()
	
	coordinates("cn.ktorfitx", "server-annotation", ktorfitxVersion)
	
	pom {
		name.set("server-annotation")
		description.set("Ktorfitx 基于 KSP2 的代码生成框架，在 Kotlin Multiplatform 中是 RESTful API 框架，在 Ktor Server 中是 路由生成框架")
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