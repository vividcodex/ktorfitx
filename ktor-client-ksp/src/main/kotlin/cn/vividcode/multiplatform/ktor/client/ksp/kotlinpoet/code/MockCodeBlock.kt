package cn.vividcode.multiplatform.ktor.client.ksp.kotlinpoet.code

import cn.vividcode.multiplatform.ktor.client.ksp.model.ClassStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.FunStructure
import cn.vividcode.multiplatform.ktor.client.ksp.model.MockModel
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
		funStructure.returnStructure.imports.forEach { packageName, simpleNames ->
			addImport(packageName, *simpleNames.toTypedArray())
		}
		val className = classStructure.className.simpleName
		val superinterfaceName = classStructure.superinterface.simpleName
		val funName = funStructure.funName
		val returnTypeName = funStructure.returnStructure.toString()
		val mockName = mockModel.name
		beginControlFlow("val mockModel = this.ktorConfig.groupMocksMap.let")
		addStatement("it[$superinterfaceName::$funName]?.get(\"$mockName\") ?: it[$className::$funName]?.get(\"${mockModel.name}\")")
		endControlFlow()
		if (mockModel.name.isEmpty()) {
			addStatement("?: error(\"$superinterfaceName 没有默认的 Mock\")")
		} else {
			addStatement("?: error(\"$superinterfaceName 没有名为 $mockName 的 Mock!\")")
		}
		addStatement("delay(mockModel.delayRange.random())")
		addStatement("return mockModel.mock as? $returnTypeName ?: error(\"返回类型和Mock数据类型不匹配\")")
	}
}