package cn.vividcode.multiplatform.ktorfitx.ksp.model.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/13 15:50
 *
 * 文件介绍：ExceptionListenerModel
 */
internal data class ExceptionListenerModel(
	val listenerClassName: ClassName,
	val exceptionTypeName: TypeName,
	val returnTypeName: TypeName,
) : FunctionModel