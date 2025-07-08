package cn.ktorfitx.multiplatform.annotation

import cn.ktorfitx.multiplatform.mock.MockProvider
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
	val provider: KClass<out MockProvider<*>>,
	val delay: Long = 0L
)