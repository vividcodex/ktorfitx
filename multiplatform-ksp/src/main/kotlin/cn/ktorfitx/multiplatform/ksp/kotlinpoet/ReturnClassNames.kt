package cn.ktorfitx.multiplatform.ksp.kotlinpoet

import cn.ktorfitx.multiplatform.ksp.constants.KtorfitxQualifiers
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/18 01:44
 *
 * 文件介绍：ReturnTypes
 */
internal object ReturnClassNames {
	
	val unit = Unit::class.asClassName()
	
	val byteArray = ByteArray::class.asClassName()
	
	val apiResult = ClassName.bestGuess(KtorfitxQualifiers.API_RESULT)
	
	val string = String::class.asClassName()
	
	val all = arrayOf(
		unit,
		byteArray,
		apiResult,
		string
	)
}