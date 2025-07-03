package cn.ktorfitx.common.ksp.util.check

import com.google.devtools.ksp.symbol.KSNode
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 编译器检查
 */
@OptIn(ExperimentalContracts::class)
inline fun <T : KSNode> T.compileCheck(
	value: Boolean,
	errorMessage: () -> String,
) {
	contract {
		returns() implies value
	}
	if (!value) {
		val message = errorMessage()
		compileCheckError(message, this)
	}
}