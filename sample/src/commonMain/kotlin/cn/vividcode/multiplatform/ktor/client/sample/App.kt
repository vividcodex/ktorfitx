package cn.vividcode.multiplatform.ktor.client.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cn.vividcode.multiplatform.ktor.client.sample.http.api.impl.testApi
import cn.vividcode.multiplatform.ktor.client.sample.http.testKtorClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
	MaterialTheme {
		val coroutineScope = rememberCoroutineScope()
		val scaffoldState = rememberScaffoldState()
		Scaffold(
			modifier = Modifier
				.fillMaxSize(),
			scaffoldState = scaffoldState
		) {
			Button(
				onClick = {
					coroutineScope.launch(Dispatchers.IO) {
						testKtorClient.testApi.testMock01("Hello", "World")
					}
				}
			) {
				Text("功能测试按钮")
			}
		}
	}
}