package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/7 上午2:36
 *
 * 介绍：KtorApiModel
 */
internal class KtorApiModel(
	val namespace: String,
	val classDeclarations: List<KSClassDeclaration>
)