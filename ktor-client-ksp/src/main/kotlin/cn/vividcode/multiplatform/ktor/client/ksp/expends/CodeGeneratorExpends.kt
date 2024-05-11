package cn.vividcode.multiplatform.ktor.client.ksp.expends

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec

/**
 * 生成代码
 */
fun CodeGenerator.generate(
	fileSpec: FileSpec,
	packageName: String,
	fileName: String
) {
	this.createNewFile(
		dependencies = Dependencies.ALL_FILES,
		packageName = packageName,
		fileName = fileName
	).bufferedWriter().use {
		fileSpec.writeTo(it)
	}
}