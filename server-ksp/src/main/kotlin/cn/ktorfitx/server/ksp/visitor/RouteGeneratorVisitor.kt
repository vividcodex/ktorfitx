package cn.ktorfitx.server.ksp.visitor

import cn.ktorfitx.common.ksp.util.expends.getKSAnnotationByType
import cn.ktorfitx.common.ksp.util.expends.getValue
import cn.ktorfitx.server.ksp.constants.ClassNames
import cn.ktorfitx.server.ksp.model.RouteGeneratorModel
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor

internal class RouteGeneratorVisitor : KSEmptyVisitor<Unit, RouteGeneratorModel?>() {
	
	override fun visitFile(file: KSFile, data: Unit): RouteGeneratorModel? {
		val annotation = file.getKSAnnotationByType(ClassNames.RouteGenerator) ?: return null
		val pathParent = annotation.getValue<String>("pathParent")!!.trim().removePrefix("/").removeSuffix("/")
		val packageName = file.packageName.asString() + ".generators"
		val funName = annotation.getValue<String>("funName")!!
		val fileName = "${file.fileName.removeSuffix(".kt")}Generator"
		return RouteGeneratorModel(pathParent, packageName, fileName, funName)
	}
	
	override fun defaultHandler(node: KSNode, data: Unit): RouteGeneratorModel? = null
}