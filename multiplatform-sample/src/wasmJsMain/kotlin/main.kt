import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import cn.ktorfitx.multiplatform.sample.App
import kotlinx.browser.document
import org.jetbrains.compose.resources.configureWebResources

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	configureWebResources {
		resourcePathMapping { path -> "./$path" }
	}
	ComposeViewport(document.body!!) {
		App()
	}
}