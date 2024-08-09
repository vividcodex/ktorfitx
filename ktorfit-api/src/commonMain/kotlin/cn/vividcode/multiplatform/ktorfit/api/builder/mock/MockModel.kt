package cn.vividcode.multiplatform.ktorfit.api.builder.mock

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/7/1 10:33
 *
 * 文件介绍：MockModel
 */
data class MockModel<T : Any> internal constructor(
	val durationRange: DurationRange,
	val result: T
)