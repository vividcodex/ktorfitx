package cn.vividcode.multiplatform.ktorfitx.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.vividcode.multiplatform.ktorfitx.sample.http.api.impl.testApi
import cn.vividcode.multiplatform.ktorfitx.sample.http.testKtorfit
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
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Button(
					onClick = {
						coroutineScope.launch(Dispatchers.IO) {
							val result = testKtorfit.testApi.test01()
							println(result)
						}
					}
				) {
					Text("功能测试按钮")
				}
			}
		}
	}
}