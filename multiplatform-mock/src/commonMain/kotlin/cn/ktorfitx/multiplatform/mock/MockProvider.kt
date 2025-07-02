package cn.ktorfitx.multiplatform.mock

interface MockProvider<out Mock : Any?> {
	
	fun provide(status: MockStatus): Mock
}