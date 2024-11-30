package cn.vividcode.multiplatform.ktorfitx.api.scope

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/4 23:40
 *
 * 文件介绍：Api 作用域
 */
interface ApiScope {
	
	/**
	 * Api 作用域名称
	 */
	val name: String
		get() = this::class.simpleName!!
}