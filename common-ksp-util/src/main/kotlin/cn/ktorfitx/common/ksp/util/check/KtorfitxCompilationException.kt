package cn.ktorfitx.common.ksp.util.check

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSNode

/**
 * Ktorfitx 编译异常
 */
private class KtorfitxCompilationException(
	message: String,
) : IllegalStateException(message)

/**
 * 编译错误
 */
fun <T : KSNode> T.ktorfitxError(
	message: () -> String
): Nothing {
	val message = message()
	val errorLocation = (this.location as? FileLocation).let {
		"\n错误位于：${if (it != null) "${it.filePath}:${it.lineNumber}" else "未知"}"
	}
	throw KtorfitxCompilationException("$message$errorLocation")
}