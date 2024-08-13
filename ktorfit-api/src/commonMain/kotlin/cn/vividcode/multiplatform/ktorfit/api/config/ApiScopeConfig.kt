package cn.vividcode.multiplatform.ktorfit.api.config

import cn.vividcode.multiplatform.ktorfit.annotation.KtorfitDsl

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/10 18:53
 *
 * 文件介绍：ApiScopeConfig
 */
@KtorfitDsl
class ApiScopeConfig internal constructor() {
	
	var printName: Boolean = false
	
	var printDescription: Boolean = false
}