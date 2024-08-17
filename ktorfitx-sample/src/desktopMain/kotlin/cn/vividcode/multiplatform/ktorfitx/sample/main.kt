package cn.vividcode.multiplatform.ktorfitx.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = "ktorfitx-sample",
	) {
		App()
	}
}