package cn.ktorfitx.common.ksp.util.check

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSNode

/**
 * Ktorfitx 编译异常
 */
private class KtorfitxCompileCheckException(
	message: String,
) : IllegalStateException(message)

/**
 * 编译检查错误
 */
fun <T : KSNode> T.compileError(
	message: () -> String
): Nothing {
	val errorLocation = (this.location as? FileLocation)
		?.let { "\n错误位于：${it.filePath}:${it.lineNumber}" }
		?: ""
	throw KtorfitxCompileCheckException("${message()}$errorLocation")
}