package cn.ktorfitx.multiplatform.ksp.check

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
internal fun <T : KSNode> compileCheckError(
	message: String,
	ksNode: T,
): Nothing {
	val errorLocation = (ksNode.location as? FileLocation)
		?.let { "\n错误位于：${it.filePath}:${it.lineNumber}" }
		?: ""
	throw KtorfitxCompileCheckException("$message$errorLocation")
}