package cn.vividcode.multiplatform.ktorfitx.api.config

import cn.vividcode.multiplatform.ktorfitx.annotation.KtorfitDsl

/**
 * 项目名称：ktorfitx
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/10 18:38
 *
 * 文件介绍：JsonConfig
 */
@KtorfitDsl
class JsonConfig internal constructor() {
	
	var prettyPrint: Boolean = true
	
	var prettyPrintIndent: String = "    "
	
	var ignoreUnknownKeys: Boolean = false
}