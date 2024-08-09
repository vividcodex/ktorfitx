package cn.vividcode.multiplatform.ktorfit.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = "vividcode-multiplatform-ktorfit-sample",
	) {
		App()
	}
}