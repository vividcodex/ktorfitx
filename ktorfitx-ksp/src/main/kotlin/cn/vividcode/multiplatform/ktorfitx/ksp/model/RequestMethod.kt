package cn.vividcode.multiplatform.ktorfitx.ksp.model

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import kotlin.reflect.KClass

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 15:50
 *
 * 文件介绍：RequestMethod
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