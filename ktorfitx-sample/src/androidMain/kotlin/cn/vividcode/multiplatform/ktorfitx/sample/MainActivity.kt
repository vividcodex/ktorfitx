package cn.vividcode.multiplatform.ktorfitx.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cn.vividcode.multiplatform.ktorfitx.sample.http.HelloWorld

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		HelloWorld().a()
		setContent {
			App()
		}
	}
}

@Preview
@Composable
fun AppAndroidPreview() {
	App()
}