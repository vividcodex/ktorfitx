package cn.vividcode.multiplatform.ktorfitx.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfitx.sample.http.api.impl.testApi
import cn.vividcode.multiplatform.ktorfitx.sample.http.testKtorfit
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
	MaterialTheme {
		val coroutineScope = rememberCoroutineScope()
		Scaffold(
			modifier = Modifier
				.fillMaxSize()
		) {
			Column(
				modifier = Modifier.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				var result: ResultBody<String>? by remember { mutableStateOf(null) }
				Spacer(modifier = Modifier.height(40.dp))
				Button(
					onClick = {
						coroutineScope.launch {
							result = testKtorfit.testApi.test01()
						}
					}
				) {
					Text("功能测试按钮")
				}
				Spacer(modifier = Modifier.height(24.dp))
				val json = Json {
					prettyPrint = true
				}
				Text(
					text = if (result != null) json.encodeToString(result) else "",
					modifier = Modifier
						.width(400.dp)
						.height(400.dp)
						.background(
							color = MaterialTheme.colorScheme.surfaceContainer,
							shape = RoundedCornerShape(8.dp)
						)
						.padding(16.dp),
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}
	}
}