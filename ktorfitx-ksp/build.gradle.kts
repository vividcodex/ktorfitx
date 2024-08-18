import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.org.jetbrains.kotlin.jvm)
	alias(libs.plugins.maven.publish)
}

val ktorfitxVersion = property("ktorfitx.version").toString()

group = "cn.vividcode.multiplatform.ktorfitx.ksp"
version = ktorfitxVersion

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
	jvmToolchain(21)
	
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_21)
	}
}

sourceSets {
	main {
		kotlin.srcDirs("build/generated/ksp/commonMain/kotlin")
		java.srcDirs("build/generated/ksp/commonMain/kotlin")
	}
}

dependencies {
	implementation(projects.ktorfitxAnnotation)
	implementation(libs.kotlin.reflect)
	implementation(libs.symbol.processing.api)
	implementation(libs.symbol.processing)
	implementation(libs.kotlinpoet.ksp)
}

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "ktorfitx-ksp", ktorfitxVersion)
	
	pom {
		name.set("ktorfitx-ksp")
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
			url.set("https://gitlab.com/vividcodex/ktorfitx")
			connection.set("scm:git:git://github.com/vividcodex/ktorfitx.git")
			developerConnection.set("scm:git:ssh://git@github.com:vividcodex/ktorfitx.git")
		}
	}
}