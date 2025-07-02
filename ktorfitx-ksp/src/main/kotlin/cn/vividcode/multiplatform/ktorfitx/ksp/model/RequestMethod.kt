package cn.vividcode.multiplatform.ktorfitx.ksp.model

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import kotlin.reflect.KClass

internal enum class RequestMethod(
	val annotation: KClass<out Annotation>,
) {
	Get(GET::class),
	
	Post(POST::class),
	
	Put(PUT::class),
	
	Delete(DELETE::class),
	
	Patch(PATCH::class),
	
	Options(OPTIONS::class),
	
	Head(HEAD::class),
}