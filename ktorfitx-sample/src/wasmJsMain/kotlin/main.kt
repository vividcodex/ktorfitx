import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import cn.vividcode.multiplatform.ktorfitx.sample.App
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	ComposeViewport(document.body!!) {
		App()
	}
}