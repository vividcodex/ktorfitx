package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * 项目：vividcode-multiplatform
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/5/7 上午2:08
 *
 * 介绍：KtorConfigModel
 */
internal class KtorClientModel(
	val namespace: String,
	val classDeclaration: KSClassDeclaration?
)