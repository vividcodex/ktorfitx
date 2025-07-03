package cn.ktorfitx.common.ksp.util.expends

import com.google.devtools.ksp.symbol.ClassKind

/**
 * 获取 ClassKind 的代码
 */
val ClassKind.code: String
	get() = this.type.replace('_', ' ')