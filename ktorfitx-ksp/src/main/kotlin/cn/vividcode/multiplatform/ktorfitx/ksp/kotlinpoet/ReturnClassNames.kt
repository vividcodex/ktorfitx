package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet

import cn.vividcode.multiplatform.ktorfitx.ksp.constants.KtorfitxQualifiers
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
	
	val resultBody = ClassName.bestGuess(KtorfitxQualifiers.RESULT_BODY)
	
	val string = String::class.asClassName()
	
	val all = arrayOf(
		unit,
		byteArray,
		resultBody,
		string
	)
}