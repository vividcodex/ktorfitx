package cn.ktorfitx.server.ksp.kotlinpoet

import cn.ktorfitx.common.ksp.util.builders.buildFileSpec
import cn.ktorfitx.common.ksp.util.builders.buildFunSpec
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilder
import cn.ktorfitx.common.ksp.util.builders.fileSpecBuilderLocal
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.constants.PackageNames
import cn.ktorfitx.server.ksp.model.AuthenticationModel
import cn.ktorfitx.server.ksp.model.RouteGeneratorModel
import cn.ktorfitx.server.ksp.model.RouteModel
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.buildCodeBlock

internal object RouteKotlinPoet {
	
	private var index = 0
	
	fun getFileSpec(
		generatorModel: RouteGeneratorModel,
		routeModels: List<RouteModel>
	): FileSpec {
		index = 0
		return buildFileSpec(generatorModel.packageName, generatorModel.fileName) {
			fileSpecBuilderLocal.set(this)
			indent("\t")
			addFunction(getFunctionSpec(generatorModel.funName, routeModels))
			fileSpecBuilderLocal.remove()
		}
	}
	
	private fun getFunctionSpec(funName: String, routeModels: List<RouteModel>): FunSpec {
		return buildFunSpec(funName) {
			receiver(ClassNames.Routing)
			addCode(getCodeBlock(routeModels))
		}
	}
	
	private fun getCodeBlock(routeModels: List<RouteModel>): CodeBlock {
		return buildCodeBlock {
			routeModels.forEach {
				if (it is AuthenticationModel) {
					fileSpecBuilder.addImport(PackageNames.KTOR_AUTH, "authenticate")
					beginControlFlow(
						"""
						authenticate(
							configurations = ${if (it.configurations.isEmpty()) "arrayOf(null)," else "arrayOf(${it.configurations.joinToString()}),"}
							strategy = %T
						)
						""".trimIndent(),
						it.strategy
					)
				}
				fileSpecBuilder.addImport(PackageNames.KTOR_ROUTING, it.requestMethod)
				beginControlFlow(
					"""
					${it.requestMethod}(
						path = %S
					)
					""".trimIndent(),
					it.path
				)
				val alias = getLetterSequence(index++)
				fileSpecBuilder.addAliasedImport(it.functionClassName, alias)
				addStatement("val result = $alias()")
				fileSpecBuilder.addImport(PackageNames.KTOR_RESPONSE, "respond")
				addStatement("call.respond(result)")
				endControlFlow()
				if (it is AuthenticationModel) {
					endControlFlow()
				}
			}
		}
	}
}

private fun getLetterSequence(index: Int): String {
	var num = index
	val builder = StringBuilder()
	do {
		builder.append(('a' + (num % 26)))
		num = num / 26 - 1
	} while (num >= 0)
	return builder.reverse().toString()
}