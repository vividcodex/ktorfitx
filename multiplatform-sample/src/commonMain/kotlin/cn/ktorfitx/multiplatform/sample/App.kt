package cn.ktorfitx.multiplatform.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cn.ktorfitx.multiplatform.sample.http.api.impls.testMethod1Api
import cn.ktorfitx.multiplatform.sample.http.api.impls.testMockApi
import cn.ktorfitx.multiplatform.sample.http.testKtorfit
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val json = Json { prettyPrint = true }

@Composable
fun App() {
	MaterialTheme {
		val coroutineScope = rememberCoroutineScope()
		Scaffold(
			modifier = Modifier
				.fillMaxSize()
		) { paddingValues ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.padding(top = 16.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				Text(
					text = "聚合数据：头条新闻（测试）",
					style = MaterialTheme.typography.titleLarge
				)
				Text(
					text = "注：接口数据由聚合数据提供",
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.colorScheme.error
				)
				var key by remember { mutableStateOf("f28b487e8ed22222fcfb7cf38df2d611") }
				OutlinedTextField(
					value = key,
					onValueChange = { key = it },
					placeholder = {
						Text(
							text = "f28b487e8ed22222fcfb7cf38df2d611",
							color = MaterialTheme.colorScheme.outline
						)
					},
					label = { Text("接口Key") },
					modifier = Modifier
						.width(380.dp)
						.padding(
							top = 16.dp,
							bottom = 8.dp
						)
				)
				var type by remember { mutableStateOf("top") }
				var isFilter by remember { mutableStateOf(0) }
				Row(
					modifier = Modifier
						.width(380.dp)
						.padding(vertical = 8.dp)
				) {
					TypeDropdown(
						value = type,
						onValueChange = { type = it },
						modifier = Modifier
							.weight(1f)
					)
					Spacer(modifier = Modifier.width(16.dp))
					FilterDetail(
						value = isFilter,
						onValueChange = { isFilter = it },
						modifier = Modifier
							.weight(1f)
					)
				}
				var page by remember {
					mutableStateOf("1", object : SnapshotMutationPolicy<String> {
						private val regex = "^(?:[1-9]|[1-4][0-9]|50)?\$".toRegex()
						override fun equivalent(a: String, b: String): Boolean {
							return !regex.matches(b)
						}
					})
				}
				var pageSize by remember {
					mutableStateOf("10", object : SnapshotMutationPolicy<String> {
						private val regex = "^(?:[1-9]|[1-2][0-9]|30)?$".toRegex()
						override fun equivalent(a: String, b: String): Boolean {
							return !regex.matches(b)
						}
					})
				}
				Row(
					modifier = Modifier
						.width(380.dp)
						.padding(vertical = 8.dp),
				) {
					OutlinedTextField(
						value = page,
						onValueChange = { page = it },
						modifier = Modifier.weight(1f),
						placeholder = { Text("默认：1，最大：50") },
						label = { Text("当前页数") },
						singleLine = true,
					)
					Spacer(modifier = Modifier.width(16.dp))
					OutlinedTextField(
						value = pageSize,
						onValueChange = { pageSize = it },
						modifier = Modifier.weight(1f),
						placeholder = { Text("默认：30，最大：30") },
						label = { Text("每页条数") },
						singleLine = true,
					)
				}
				var text: String? by remember { mutableStateOf(null) }
				Button(
					modifier = Modifier
						.padding(vertical = 8.dp)
						.width(380.dp)
						.clip(RoundedCornerShape(4.dp)),
					onClick = {
						coroutineScope.launch {
							val correctKey = key.ifBlank { "f28b487e8ed22222fcfb7cf38df2d611" }
							val result = testKtorfit.testMethod1Api.headlineNews(correctKey, type, page, pageSize, isFilter).getOrNull()
							if (result == null) {
								text = ""
								return@launch
							}
							val jsonElement = json.parseToJsonElement(result)
							text = json.encodeToString(jsonElement)
						}
					}
				) {
					Text("点击查询")
				}
				Spacer(modifier = Modifier.height(8.dp))
				
				Button(
					modifier = Modifier
						.padding(vertical = 8.dp)
						.width(380.dp)
						.clip(RoundedCornerShape(4.dp)),
					onClick = {
						coroutineScope.launch {
							val result = testKtorfit.testMockApi.mockTest2().getOrNull()
							text = result
						}
					}
				) {
					Text("测试 Mock")
				}
				
				val horizontalScrollState = rememberScrollState()
				val verticalScrollState = rememberScrollState()
				Text(
					text = text ?: "",
					modifier = Modifier
						.padding(8.dp)
						.fillMaxWidth()
						.weight(1f)
						.background(
							color = MaterialTheme.colorScheme.surfaceContainer,
							shape = RoundedCornerShape(8.dp)
						)
						.padding(vertical = 8.dp)
						.horizontalScroll(horizontalScrollState)
						.verticalScroll(verticalScrollState)
						.padding(
							horizontal = 16.dp,
							vertical = 8.dp
						),
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeDropdown(
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	var typeExpanded by remember { mutableStateOf(false) }
	val options = mapOf(
		"top" to "推荐",
		"guonei" to "国内",
		"guoji" to "国际",
		"yule" to "娱乐",
		"tiyu" to "体育",
		"junshi" to "军事",
		"keji" to "科技",
		"caijing" to "财经",
		"youxi" to "游戏",
		"qiche" to "汽车",
		"jiankang" to "健康"
	)
	ExposedDropdownMenuBox(
		expanded = typeExpanded,
		onExpandedChange = { typeExpanded = !typeExpanded },
		modifier = modifier
	) {
		OutlinedTextField(
			value = options[value]!!,
			onValueChange = { },
			modifier = modifier
				.menuAnchor(MenuAnchorType.SecondaryEditable),
			readOnly = true,
			label = { Text("类型") },
			colors = ExposedDropdownMenuDefaults.textFieldColors(),
			trailingIcon = {
				ExposedDropdownMenuDefaults.TrailingIcon(
					expanded = typeExpanded
				)
			}
		)
		ExposedDropdownMenu(
			expanded = typeExpanded,
			onDismissRequest = { typeExpanded = false },
			modifier = modifier
		) {
			options.forEach { (key, value) ->
				DropdownMenuItem(
					text = { Text(value) },
					onClick = {
						onValueChange(key)
						typeExpanded = false
					}
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDetail(
	value: Int,
	onValueChange: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	var typeExpanded by remember { mutableStateOf(false) }
	val options = listOf("否", "是")
	ExposedDropdownMenuBox(
		expanded = typeExpanded,
		onExpandedChange = { typeExpanded = !typeExpanded },
		modifier = modifier
	) {
		OutlinedTextField(
			value = options[value],
			onValueChange = { },
			modifier = modifier
				.menuAnchor(MenuAnchorType.SecondaryEditable),
			readOnly = true,
			label = { Text("必须包含详情信息") },
			colors = ExposedDropdownMenuDefaults.textFieldColors(),
			trailingIcon = {
				ExposedDropdownMenuDefaults.TrailingIcon(
					expanded = typeExpanded
				)
			}
		)
		ExposedDropdownMenu(
			expanded = typeExpanded,
			onDismissRequest = { typeExpanded = false },
			modifier = modifier
		) {
			options.forEachIndexed { index, s ->
				DropdownMenuItem(
					text = { Text(s) },
					onClick = {
						onValueChange(index)
						typeExpanded = false
					}
				)
			}
		}
	}
}