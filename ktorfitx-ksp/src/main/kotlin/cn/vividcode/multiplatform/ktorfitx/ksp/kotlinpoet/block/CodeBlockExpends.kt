package cn.vividcode.multiplatform.ktorfitx.ksp.kotlinpoet.block

import cn.vividcode.multiplatform.ktorfitx.ksp.model.EncryptInfo

internal fun String.encryptVarName(encryptInfo: EncryptInfo?): String {
	if (encryptInfo == null) return this
	UseImports.addImports("cn.vividcode.multiplatform.ktorfitx.api.encrypt", "EncryptType", "HexType", "encrypt")
	val (encryptType, hexType, layer) = encryptInfo
	return "$this.encrypt(EncryptType.$encryptType, HexType.$hexType, $layer)"
}