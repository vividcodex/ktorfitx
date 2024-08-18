package cn.vividcode.multiplatform.ktorfitx.annotation

import cn.vividcode.multiplatform.ktorfitx.api.mock.MockProvider
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockStatus
import kotlin.reflect.KClass

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/12 03:40
 *
 * 文件介绍：Mock
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
	val provider: KClass<out MockProvider<*>>,
	val status: MockStatus = MockStatus.SUCCESS,
	val delayRange: LongArray = [200L]
)