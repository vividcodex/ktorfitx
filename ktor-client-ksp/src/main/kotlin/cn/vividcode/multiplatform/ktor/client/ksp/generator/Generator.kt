package cn.vividcode.multiplatform.ktor.client.ksp.generator

import com.squareup.kotlinpoet.ClassName

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/7 上午2:20
 *
 * 介绍：Generator
 */
internal sealed interface Generator<T : Any> {
	
	private companion object {
		private val canonicalNameList = mutableListOf<String>()
	}
	
	fun exists(className: ClassName): Boolean {
		val exists = canonicalNameList.contains(className.canonicalName)
		if (!exists) {
			canonicalNameList += className.canonicalName
		}
		return exists
	}
	
	fun process(data: T)
}