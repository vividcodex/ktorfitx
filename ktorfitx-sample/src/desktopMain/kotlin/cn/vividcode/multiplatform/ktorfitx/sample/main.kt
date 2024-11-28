package cn.vividcode.multiplatform.ktorfitx.sample

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
	Window(
		onCloseRequest = ::exitApplication,
		title = "ktorfitx-sample",
		state = rememberWindowState(
			position = WindowPosition.Aligned(Alignment.Center),
			size = DpSize(460.dp, 750.dp)
		),
		resizable = false
	) {
		App()
	}
}