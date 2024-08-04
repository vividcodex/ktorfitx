package cn.vividcode.multiplatform.ktor.client.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = "vividcode-multiplatform-ktor-client-sample",
	) {
		App()
	}
}