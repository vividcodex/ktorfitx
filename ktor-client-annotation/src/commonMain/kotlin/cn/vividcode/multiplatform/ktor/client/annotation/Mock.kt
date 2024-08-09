package cn.vividcode.multiplatform.ktor.client.annotation

/**
 * 项目名称：vividcode-multiplatform-ktor-client
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 10:23
 *
 * 文件介绍：Mock
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
	val name: String = "DEFAULT"
)