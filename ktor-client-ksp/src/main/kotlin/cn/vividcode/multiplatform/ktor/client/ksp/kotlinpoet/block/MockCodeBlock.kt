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
		addImports(funStructure.returnStructure.classNames)
		val superinterfaceName = classStructure.superinterface.simpleName
		val className = classStructure.className.simpleName
		val funName = funStructure.funName
		val mockName = mockModel.name
		addStatement("return this.mockClient.getMock($superinterfaceName::$funName, $className::$funName, \"$mockName\")")
	}
}