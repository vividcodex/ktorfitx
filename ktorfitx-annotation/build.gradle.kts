import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.android.library)
	alias(libs.plugins.maven.publish)
}

val ktorfitxVersion = property("ktorfitx.version").toString()
val ktorfitxAutomaticRelease = property("ktorfitx.automaticRelease").toString().toBoolean()

group = "cn.vividcode.multiplatform.ktorfitx.annotation"
version = ktorfitxVersion

kotlin {
	jvmToolchain(21)
	
	androidTarget {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
	
	jvm("desktop") {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
	
	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.apply {
			binaries.framework {
				baseName = "KtorfitxAnnotation"
				isStatic = true
			}
			compilerOptions {
				languageVersion = KotlinVersion.KOTLIN_2_2
				apiVersion = KotlinVersion.KOTLIN_2_2
			}
		}
	}
	
	js {
		outputModuleName = "ktorfitxAnnotation"
		browser {
			commonWebpackConfig {
				outputFileName = "ktorfitxAnnotation.js"
			}
		}
		binaries.executable()
		useEsModules()
		compilerOptions {
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
	
	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		outputModuleName = "ktorfitxAnnotation"
		browser {
			val rootDirPath = project.rootDir.path
			val projectDirPath = project.projectDir.path
			commonWebpackConfig {
				outputFileName = "ktorfitxAnnotation.js"
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
					static = (static ?: mutableListOf()).apply {
						add(rootDirPath)
						add(projectDirPath)
					}
				}
			}
		}
		binaries.executable()
		useEsModules()
		compilerOptions {
			languageVersion = KotlinVersion.KOTLIN_2_2
			apiVersion = KotlinVersion.KOTLIN_2_2
		}
	}
	
	compilerOptions {
		languageVersion = KotlinVersion.KOTLIN_2_2
		apiVersion = KotlinVersion.KOTLIN_2_2
	}
}

android {
	namespace = "cn.vividcode.multiplatform.ktorfitx.annotation"
	compileSdk = libs.versions.android.compileSdk.get().toInt()
	
	sourceSets["main"].apply {
		manifest.srcFile("src/androidMain/AndroidManifest.xml")
		res.srcDirs("src/androidMain/res")
		resources.srcDirs("src/commonMain/resources")
	}
	
	defaultConfig {
		minSdk = libs.versions.android.minSdk.get().toInt()
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
			merges += "/META-INF/DEPENDENCIES"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
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
	
	coordinates("cn.vividcode.multiplatform", "ktorfitx-annotation", ktorfitxVersion)
	
	pom {
		name.set("ktorfitx-api")
		description.set("Ktorfitx 基于 Ktor 的 RESTful API 框架")
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
			url.set("https://github.com/vividcodex/ktorfitx")
			connection.set("scm:git:git://github.com/vividcodex/ktorfitx.git")
			developerConnection.set("scm:git:ssh://git@github.com:vividcodex/ktorfitx.git")
		}
	}
}