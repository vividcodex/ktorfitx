package cn.ktorfitx.multiplatform.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

@Suppress("unused")
class KtorfitxMultiplatformPlugin : Plugin<Project> {
	
	private companion object {
		
		private const val VERSION = "3.2.3-3.0.4"
		
		private const val KOTLIN_MULTIPLATFORM = "org.jetbrains.kotlin.multiplatform"
		private const val KSP = "com.google.devtools.ksp"
		
		private const val MULTIPLATFORM_ANNOTATION = "cn.ktorfitx:multiplatform-annotation"
		private const val MULTIPLATFORM_CORE = "cn.ktorfitx:multiplatform-core"
		private const val MULTIPLATFORM_KSP = "cn.ktorfitx:multiplatform-ksp"
		private const val MULTIPLATFORM_MOCK = "cn.ktorfitx:multiplatform-mock"
		private const val MULTIPLATFORM_WEBSOCKETS = "cn.ktorfitx:multiplatform-websockets"
	}
	
	override fun apply(target: Project) {
		val extension = target.extensions.create("ktorfitx", KtorfitxMultiplatformExtension::class.java)
		target.pluginManager.apply(KOTLIN_MULTIPLATFORM)
		target.pluginManager.apply(KSP)
		target.afterEvaluate {
			extensions.getByType<KotlinMultiplatformExtension>().apply {
				sourceSets.getByName("commonMain") {
					dependencies {
						implementation("$MULTIPLATFORM_ANNOTATION:$VERSION")
						implementation("$MULTIPLATFORM_CORE:$VERSION")
						if (extension.websockets.enabled.get()) {
							implementation("$MULTIPLATFORM_WEBSOCKETS:$VERSION")
						}
						if (extension.mock.enabled.get()) {
							implementation("$MULTIPLATFORM_MOCK:$VERSION")
						}
					}
					kotlin.srcDir(extension.ksp.kspGeneratedSrcDir.get())
				}
			}
			
			if (extension.ksp.kspMetadataGenerationTask.get()) {
				tasks.withType<KotlinCompilationTask<*>>().all {
					"kspCommonMainKotlinMetadata".also {
						if (name != it) dependsOn(it)
					}
				}
			}
		}
		target.dependencies {
			add("kspCommonMainMetadata", "$MULTIPLATFORM_KSP:$VERSION")
		}
	}
}