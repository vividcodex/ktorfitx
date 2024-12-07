package cn.vividcode.multiplatform.ktorfitx.ksp.check

import cn.vividcode.multiplatform.ktorfitx.ksp.kspLogger
import com.google.devtools.ksp.symbol.KSNode
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 编译器检查
 */
@OptIn(ExperimentalContracts::class)
internal inline fun <T : KSNode> T.compileCheck(
	value: Boolean,
	lazyErrorMessage: () -> String,
) {
	contract {
		returns() implies value
	}
	if (!value) {
		val message = lazyErrorMessage()
		kspLogger?.error("\nKtorfitx编译期错误检查: $message", this)
		compileCheckError(message, this)
	}
}