package cn.ktorfitx.multiplatform.mock

interface MockProvider<out R> {
	
	@Throws(Throwable::class)
	fun provide(): R
}