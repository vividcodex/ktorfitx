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
 * 介绍：RequestMethod
 */
internal enum class RequestMethod(
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