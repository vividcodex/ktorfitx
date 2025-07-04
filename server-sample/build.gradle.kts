import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ktor)
	alias(libs.plugins.ksp)
}

group = "cn.ktorfitx.server.sample"
version = property("ktorfitx.sample.version").toString()

application {
	mainClass = "io.ktor.server.netty.EngineMain"
	
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

kotlin {
	jvmToolchain(21)
	
	compilerOptions {
		languageVersion = KotlinVersion.KOTLIN_2_2
		apiVersion = KotlinVersion.KOTLIN_2_2
		jvmTarget = JvmTarget.JVM_21
	}
}

dependencies {
	implementation(projects.serverAnnotation)
	implementation(projects.serverAuth)
	implementation(projects.serverWebsockets)
	implementation(libs.bundles.server.sample)
	
	ksp(projects.serverKsp)
}