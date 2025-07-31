plugins {
	`java-gradle-plugin`
	`kotlin-dsl`
}

group = "cn.ktorfitx.multiplatform.plugin"
version = "3.2.3-3.1.0-dev01"

repositories {
	mavenCentral()
	google()
	gradlePluginPortal()
}

gradlePlugin {
	plugins {
		create("KotrfitxMultiplatformPlugin") {
			id = "cn.ktorfitx.multiplatform"
			implementationClass = "cn.ktorfitx.multiplatform.plugin.KtorfitxMultiplatformPlugin"
		}
	}
}

dependencies {
	//noinspection UseTomlInstead
	implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.2.0-2.0.2")
	implementation("org.jetbrains.kotlin.multiplatform:org.jetbrains.kotlin.multiplatform.gradle.plugin:2.2.0")
}