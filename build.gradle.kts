plugins {
	alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.compose.compiler) apply false
	alias(libs.plugins.kotlin.serialization) apply false
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.android.library) apply false
	alias(libs.plugins.jetbrains.compose) apply false
	alias(libs.plugins.maven.publish) apply false
}

tasks.register("ktorfitxPublishAllPublicationsToMavenCentralRepository") {
	group = "publishing"
	
	dependsOn(
		":ktorfitx-annotation:publishAllPublicationsToMavenCentralRepository",
		":ktorfitx-core:publishAllPublicationsToMavenCentralRepository",
		":ktorfitx-mock:publishAllPublicationsToMavenCentralRepository",
		":ktorfitx-websockets:publishAllPublicationsToMavenCentralRepository",
		":ktorfitx-ksp:publishAllPublicationsToMavenCentralRepository"
	)
}