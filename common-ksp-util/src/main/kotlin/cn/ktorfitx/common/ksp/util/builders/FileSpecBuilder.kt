package cn.ktorfitx.common.ksp.util.builders

import com.squareup.kotlinpoet.FileSpec

val fileSpecBuilderLocal = ThreadLocal<FileSpec.Builder>()

val fileSpecBuilder: FileSpec.Builder
	get() = fileSpecBuilderLocal.get()!!