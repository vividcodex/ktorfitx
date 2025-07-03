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
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		google()
		mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

include("multiplatform-annotation")
include("multiplatform-core")
include("multiplatform-mock")
include("multiplatform-websockets")
include("multiplatform-ksp")
include("multiplatform-sample")

include("server-annotation")
include("server-auth")
include("server-websockets")
include("server-ksp")
include("server-sample")

include("common-ksp-util")