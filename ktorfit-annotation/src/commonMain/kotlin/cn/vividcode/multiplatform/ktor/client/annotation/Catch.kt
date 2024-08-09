package cn.vividcode.multiplatform.ktorfit.annotation

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/5 14:17
 *
 * 文件介绍：Catch
 */
fun interface Catch<E : Exception> {
	
	fun run(e: E)
}