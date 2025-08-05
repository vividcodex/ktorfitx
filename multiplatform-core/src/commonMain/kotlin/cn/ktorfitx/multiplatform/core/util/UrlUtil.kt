package cn.ktorfitx.multiplatform.core.util

object UrlUtil {
	
	private const val HTTP = "http://"
	private const val HTTPS = "https://"
	private const val WS = "ws://"
	private const val WSS = "wss://"
	
	fun parseUrl(
		rawUrl: String,
		apiUrl: String?,
		paths: Map<String, Any>
	): String {
		val initialUrl = when {
			rawUrl.isWSOrWSS() -> error("HTTP 协议不允许开头使用 ws:// 或者 wss://")
			apiUrl == null || rawUrl.isHttpOrHttps() -> rawUrl
			else -> "$apiUrl/$rawUrl"
		}
		return paths.toList().fold(initialUrl) { acc, it ->
			acc.replace("{${it.first}}", it.second.toString())
		}
	}
	
	private fun String.isHttpOrHttps(): Boolean {
		return this.startsWith(HTTP) || this.startsWith(HTTPS)
	}
	
	private fun String.isWSOrWSS(): Boolean {
		return this.startsWith(WS) || this.startsWith(WSS)
	}
}