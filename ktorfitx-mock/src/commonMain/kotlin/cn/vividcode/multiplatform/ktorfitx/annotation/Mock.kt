package cn.vividcode.multiplatform.ktorfitx.annotation

import cn.vividcode.multiplatform.ktorfitx.mock.MockProvider
import cn.vividcode.multiplatform.ktorfitx.mock.MockStatus
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
	val provider: KClass<out MockProvider<*>>,
	val status: MockStatus = MockStatus.SUCCESS,
	val delayRange: LongArray = [200L]
)