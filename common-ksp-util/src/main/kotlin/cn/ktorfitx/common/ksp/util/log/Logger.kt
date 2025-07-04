package cn.ktorfitx.common.ksp.util.log

import com.google.devtools.ksp.processing.KSPLogger

val kspLoggerLocal = ThreadLocal<KSPLogger>()

val kspLogger: KSPLogger?
	get() = kspLoggerLocal.get()