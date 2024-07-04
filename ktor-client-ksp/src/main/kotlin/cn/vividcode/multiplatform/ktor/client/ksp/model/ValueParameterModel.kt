package cn.vividcode.multiplatform.ktor.client.ksp.model

import com.squareup.kotlinpoet.ClassName

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午3:53
 *
 * 介绍：ValueParameterModel
 */
internal sealed interface ValueParameterModel {
	
	val varName: String
	
	val className: ClassName
}