package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktor.client.ksp.model.model.MockModel
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.ClassStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.structure.FunStructure
import com.squareup.kotlinpoet.CodeBlock

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/4 下午11:07
 *
 * 介绍：MockCodeBlock
 */
internal class MockCodeBlock(
	addImport: (String, Array<out String>) -> Unit,
) : BuildCodeBlock(addImport) {
	
	/**
	 * 构建 Mock
	 */
	fun CodeBlock.Builder.buildMockCodeBlock(
		classStructure: ClassStructure,
		funStructure: FunStructure,
		mockModel: MockModel
	) {
		addImport("kotlinx.coroutines", "delay")
		addImports(funStructure.returnStructure.classNames)
		val className = classStructure.className.simpleName
		val superinterfaceName = classStructure.superinterface.simpleName
		val funName = funStructure.funName
		val returnSimpleName = funStructure.returnStructure.simpleName
		val mockName = mockModel.name
		beginControlFlow("val mockModel = this.ktorConfig.groupMocksMap.let")
		addStatement("it[$superinterfaceName::$funName]?.get(\"$mockName\") ?: it[$className::$funName]?.get(\"$mockName\")")
		endControlFlow()
		if (mockName.isEmpty()) {
			addStatement("?: error(\"$funName 没有默认的 Mock\")")
		} else {
			addStatement("?: error(\"$funName 没有名为 $mockName 的 Mock!\")")
		}
		addStatement("delay(mockModel.delayRange.random())")
		addStatement("return mockModel.mock as? $returnSimpleName ?: error(\"返回类型和 Mock 数据类型不匹配\")")
	}
}