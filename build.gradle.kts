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

tasks.register("publishKtorfitxToMavenCentralRepository") {
	group = "publishing"
	
	val projects = arrayOf(
		"common-ksp-util",
		"multiplatform-annotation",
		"multiplatform-core",
		"multiplatform-mock",
		"multiplatform-websockets",
		"multiplatform-ksp",
		"server-annotation",
		"server-auth",
		"server-websockets",
		"server-ksp"
	).map {
		":$it:publishAllPublicationsToMavenCentralRepository"
	}.toTypedArray()
	
	dependsOn(*projects)
}