package cn.vividcode.multiplatform.ktor.client.api.annotation

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/6/27 下午10:12
 *
 * 介绍：Mock
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
	val name: String
)