package cn.vividcode.multiplatform.ktor.client.api.exception

/**
 * 项目：vividcode-multiplatform-ktor-client
 *
 * 作者：li-jia-wei
 *
 * 创建：2024/7/5 下午2:17
 *
 * 介绍：CatchCallback
 */
fun interface CatchCallback<E : Exception> {
	
	fun run(e: E)
}