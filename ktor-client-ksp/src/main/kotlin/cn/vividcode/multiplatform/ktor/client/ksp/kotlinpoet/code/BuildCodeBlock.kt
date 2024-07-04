package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.code

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/4 下午11:31
 *
 * 介绍：BuildCodeBlock
 */
internal sealed class BuildCodeBlock(
	private val addImport: (String, Array<out String>) -> Unit
) {
	
	fun addImport(packageName: String, vararg simpleNames: String) {
		this.addImport(packageName, simpleNames)
	}
}