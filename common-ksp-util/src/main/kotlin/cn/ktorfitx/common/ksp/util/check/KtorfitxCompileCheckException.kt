package cn.ktorfitx.common.ksp.util.check

import com.google.devtools.ksp.processing.KSPLogger
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
fun <T : KSNode> compileCheckError(
	message: String,
	ksNode: T,
): Nothing {
	kspLogger?.error("\nKtorfitx 编译期错误检查: $message", ksNode)
	val errorLocation = (ksNode.location as? FileLocation)
		?.let { "\n错误位于：${it.filePath}:${it.lineNumber}" }
		?: ""
	throw KtorfitxCompileCheckException("$message$errorLocation")
}

val kspLoggerLocal = ThreadLocal<KSPLogger>()

private val kspLogger: KSPLogger?
	get() = kspLoggerLocal.get()