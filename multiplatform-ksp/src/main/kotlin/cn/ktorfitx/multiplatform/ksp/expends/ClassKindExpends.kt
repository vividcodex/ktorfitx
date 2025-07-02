package cn.ktorfitx.multiplatform.ksp.expends

import com.google.devtools.ksp.symbol.ClassKind

/**
 * 获取 ClassKind 的代码
 */
internal val ClassKind.code: String
	get() = this.type.replace('_', ' ')