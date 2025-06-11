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
	errorMessage: () -> String,
) {
	contract {
		returns() implies value
	}
	if (!value) {
		val message = errorMessage()
		kspLogger?.error("\nKtorfitx 编译期错误检查: $message\n详情请查看官方文档: $KTORFITX_DOCUMENT_URL", this)
		compileCheckError(message, this)
	}
}

private const val KTORFITX_DOCUMENT_URL = "https://vividcodex.github.io/ktorfitx-document/index_md.html"