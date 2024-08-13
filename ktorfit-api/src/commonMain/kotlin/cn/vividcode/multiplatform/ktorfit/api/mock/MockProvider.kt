package cn.vividcode.multiplatform.ktorfit.api.mock

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/12 03:42
 *
 * 文件介绍：MockProvider
 */
interface MockProvider<out Mock : Any> {
	
	fun provide(status: MockStatus): Mock
}