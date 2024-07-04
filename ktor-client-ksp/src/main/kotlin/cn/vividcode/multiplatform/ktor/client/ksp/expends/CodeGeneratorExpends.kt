package cn.vividcode.multiplatform.ktor.client.ksp.expends

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec

/**
 * 生成代码
 */
fun CodeGenerator.generate(
	fileSpec: FileSpec,
	className: ClassName
) {
	this.createNewFile(
		dependencies = Dependencies.ALL_FILES,
		packageName = className.packageName,
		fileName = className.simpleName
	).bufferedWriter().use(fileSpec::writeTo)
}