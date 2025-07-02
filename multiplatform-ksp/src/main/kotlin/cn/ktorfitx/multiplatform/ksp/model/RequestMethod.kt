package cn.ktorfitx.multiplatform.ksp.model

import cn.ktorfitx.multiplatform.annotation.*
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