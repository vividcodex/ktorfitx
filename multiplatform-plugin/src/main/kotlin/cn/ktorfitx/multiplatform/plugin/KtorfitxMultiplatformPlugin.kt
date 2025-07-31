package cn.ktorfitx.multiplatform.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import javax.inject.Inject

@Suppress("unused")
class KtorfitxMultiplatformPlugin : Plugin<Project> {
	
	private companion object {
		
		private const val KMP = "org.jetbrains.kotlin.multiplatform"
		private const val KSP = "com.google.devtools.ksp"
		
		private const val VERSION = "3.2.3-3.0.3"
		private const val GROUP = "cn.ktorfitx"
		
		private const val MULTIPLATFORM_ANNOTATION = "$GROUP:multiplatform-annotation:$VERSION"
		private const val MULTIPLATFORM_CORE = "$GROUP:multiplatform-core:$VERSION"
		private const val MULTIPLATFORM_KSP = "$GROUP:multiplatform-ksp:$VERSION"
		private const val MULTIPLATFORM_MOCK = "$GROUP:multiplatform-mock:$VERSION"
		private const val MULTIPLATFORM_WEBSOCKETS = "$GROUP:multiplatform-websockets:$VERSION"
	}
	
	override fun apply(target: Project) {
		val extension = target.extensions.create("ktorfitx", KtorfitxMultiplatformExtension::class.java)
		target.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
		target.pluginManager.apply("com.google.devtools.ksp")
		target.afterEvaluate {
			this.extensions.getByType<KotlinMultiplatformExtension>().sourceSets.getByName("commonMain").dependencies {
				implementation(MULTIPLATFORM_ANNOTATION)
				implementation(MULTIPLATFORM_CORE)
				if (extension.websocketsEnabled.getOrElse(false)) {
					implementation(MULTIPLATFORM_WEBSOCKETS)
				}
				if (extension.mockEnabled.getOrElse(false)) {
					implementation(MULTIPLATFORM_MOCK)
				}
			}
			
			dependencies.add("kspCommonMainMetadata", MULTIPLATFORM_KSP)
		}
	}
}

open class KtorfitxMultiplatformExtension @Inject constructor(
	objects: ObjectFactory
) {
	val websocketsEnabled = objects.property<Boolean>()
	val mockEnabled = objects.property<Boolean>()
}