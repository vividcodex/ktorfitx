rootProject.name = "ktorfitx"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
	repositories {
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		google()
		gradlePluginPortal()
		mavenCentral()
	}
}
plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		google()
		mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}
include("ktorfitx-api")
include("ktorfitx-websockets")
include("ktorfitx-ksp")
include("ktorfitx-annotation")
include("ktorfitx-sample")