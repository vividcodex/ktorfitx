package cn.vividcode.multiplatform.ktorfitx.mock

interface MockProvider<out Mock : Any?> {
	
	fun provide(status: MockStatus): Mock
}