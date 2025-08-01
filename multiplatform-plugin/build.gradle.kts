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
			displayName = "Kotrfitx Multiplatform Plugin"
			implementationClass = "cn.ktorfitx.multiplatform.plugin.KtorfitxMultiplatformPlugin"
		}
	}
}

dependencies {
	implementation(libs.bundles.multiplatform.plugin)
}