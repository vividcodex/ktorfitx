import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import cn.ktorfitx.multiplatform.sample.App
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	onWasmReady {
		CanvasBasedWindow("KtorfitxSample") {
			App()
		}
	}
}