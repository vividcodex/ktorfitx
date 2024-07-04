package cn.vividcode.multiplatform.ktor.client.ksp.model

import cn.vividcode.multiplatform.ktor.client.api.annotation.*
import kotlin.reflect.KClass

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/1 下午3:50
 *
 * 介绍：RequestType
 */
internal enum class RequestType(
	val annotation: KClass<out Annotation>
) {
	Get(GET::class),
	
	Post(POST::class),
	
	Put(PUT::class),
	
	Delete(DELETE::class),
	
	Patch(PATCH::class),
	
	Options(OPTIONS::class),
	
	Head(HEAD::class),
}