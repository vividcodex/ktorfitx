package cn.vividcode.multiplatform.ktorfitx.api.mock

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/12 03:42
 *
 * 文件介绍：MockProvider
 */
interface MockProvider<out Mock : Any?> {
	
	fun provide(status: MockStatus): Mock
}