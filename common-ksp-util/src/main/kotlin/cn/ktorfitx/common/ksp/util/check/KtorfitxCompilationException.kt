package cn.ktorfitx.common.ksp.util.check

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSNode

/**
 * Ktorfitx 编译异常
 */
private class KtorfitxCompilationException(
	message: String,
	location: FileLocation?
) : IllegalStateException("$message\n错误位于：${parseKtorfitxExceptionMessage(location)}")

private fun parseKtorfitxExceptionMessage(location: FileLocation?): String {
	return if (location != null) "${location.filePath}:${location.lineNumber}" else "未知"
}

/**
 * 编译错误
 */
fun <T : KSNode> T.ktorfitxError(
	message: () -> String
): Nothing {
	val message = message()
	val location = this.location as? FileLocation
	throw KtorfitxCompilationException(message, location)
}