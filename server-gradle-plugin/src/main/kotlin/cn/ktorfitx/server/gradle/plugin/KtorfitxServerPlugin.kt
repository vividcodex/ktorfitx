package cn.ktorfitx.server.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class KtorfitxServerPlugin : Plugin<Project> {
	
	private companion object Companion {
		
		private const val VERSION = "3.2.3-3.0.4"
	}
	
	override fun apply(target: Project) {
		val extension = target.extensions.create("ktorfitx", KtorfitxServerExtension::class.java)
		target.pluginManager.apply("com.google.devtools.ksp")
		
		target.dependencies {
			implementation(group = "cn.ktorfitx", name = "server-core")
			implementation(group = "cn.ktorfitx", name = "server-annotation")
			ksp(group = "cn.ktorfitx", name = "server-ksp")
		}
		
		target.afterEvaluate {
			dependencies {
				if (extension.auth.enabled.get()) {
					implementation(group = "cn.ktorfitx", name = "server-auth")
				}
				if (extension.websockets.enabled.get()) {
					implementation(group = "cn.ktorfitx", name = "server-websockets")
				}
			}
		}
	}
	
	private fun DependencyHandlerScope.implementation(group: String, name: String): Dependency? =
		add("implementation", "$group:$name:$VERSION")
	
	private fun DependencyHandlerScope.ksp(group: String, name: String): Dependency? =
		add("ksp", "$group:$name:$VERSION")
}