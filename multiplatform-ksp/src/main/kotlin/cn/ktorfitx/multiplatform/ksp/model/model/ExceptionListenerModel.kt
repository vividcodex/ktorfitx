package cn.ktorfitx.multiplatform.ksp.model.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

internal class ExceptionListenerModel(
	val listenerClassName: ClassName,
	val exceptionTypeName: TypeName,
	val returnTypeName: TypeName,
) : FunctionModel