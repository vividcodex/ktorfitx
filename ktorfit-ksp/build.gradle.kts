import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.org.jetbrains.kotlin.jvm)
	alias(libs.plugins.maven.publish)
}

val ktorfitVersion = property("ktorfit.version").toString()

group = "cn.vividcode.multiplatform.ktorfit.ksp"
version = ktorfitVersion

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
	implementation(projects.ktorfitAnnotation)
	implementation(libs.kotlin.reflect)
	implementation(libs.symbol.processing.api)
	implementation(libs.symbol.processing)
	implementation(libs.kotlinpoet.ksp)
}

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
	signAllPublications()
	
	coordinates("cn.vividcode.multiplatform", "ktorfit-ksp", ktorfitVersion)
	
	pom {
		name.set("ktorfit-ksp")
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
			url.set("https://gitlab.com/vividcodex/ktorfit")
			connection.set("scm:git:git://github.com/vividcodex/ktorfit.git")
			developerConnection.set("scm:git:ssh://git@github.com:vividcodex/ktorfit.git")
		}
	}
}