package cn.ktorfitx.multiplatform.ksp.model

import cn.ktorfitx.multiplatform.ksp.constants.ClassNames
import com.squareup.kotlinpoet.ClassName

internal enum class RequestMethod(
	val className: ClassName
) {
	Get(ClassNames.GET),
	
	Post(ClassNames.POST),
	
	Put(ClassNames.PUT),
	
	Delete(ClassNames.DELETE),
	
	Patch(ClassNames.PATCH),
	
	Options(ClassNames.OPTIONS),
	
	Head(ClassNames.HEAD),
}