package cn.ktorfitx.multiplatform.gradle.plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class KtorfitxMultiplatformExtension @Inject constructor(
	objects: ObjectFactory
) {
	
	val websockets = objects.newInstance(WebsocketsConfig::class.java)
	val mock = objects.newInstance(MockConfig::class.java)
	val ksp = objects.newInstance(KspConfig::class.java)
	
	fun websockets(action: WebsocketsConfig.() -> Unit) {
		websockets.action()
	}
	
	fun mock(action: MockConfig.() -> Unit) {
		mock.action()
	}
	
	fun ksp(action: KspConfig.() -> Unit) {
		ksp.action()
	}
}

open class WebsocketsConfig @Inject constructor(objects: ObjectFactory) {
	val enabled = objects.property<Boolean>().convention(false)
}

open class MockConfig @Inject constructor(objects: ObjectFactory) {
	val enabled = objects.property<Boolean>().convention(false)
}

open class KspConfig @Inject constructor(objects: ObjectFactory) {
	val kspMetadataGenerationTask = objects.property<Boolean>().convention(true)
	val kspGeneratedSrcDir = objects.property<String>().convention(
		"build/generated/ksp/metadata/commonMain/kotlin"
	)
}