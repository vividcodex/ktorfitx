package cn.ktorfitx.multiplatform.annotation

import cn.ktorfitx.multiplatform.mock.MockProvider
import cn.ktorfitx.multiplatform.mock.MockStatus
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
	val provider: KClass<out MockProvider<*>>,
	val status: MockStatus = MockStatus.SUCCESS,
	val delayRange: LongArray = [200L]
)