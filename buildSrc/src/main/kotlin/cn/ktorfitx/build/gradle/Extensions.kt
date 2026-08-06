package cn.ktorfitx.build.gradle

import org.gradle.api.provider.Provider

fun Provider<String>.getInt(): Int = this.get().toInt()

