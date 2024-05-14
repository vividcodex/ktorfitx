# Kotlin 多平台 KtorClient 代码生成器

## 版本说明

ktor版本-代码生成器版本：例如：`2.3.11`-`1.0.3`

Kotlin：1.9.23

Ktor：2.3.11

## 最新版本

`2.3.11`-`1.0.3`

## 依赖说明

模块中已经包含了以下依赖，不需要额外重新添加

``` kotlin
io.ktor:ktor-client-core:2.3.11
io.ktor:ktor-client-logging:2.3.11
io.ktor:ktor-client-cio:2.3.11
io.ktor:ktor-client-serialization:2.3.11
io.ktor:ktor-client-content-negotiation:2.3.11
io.ktor:ktor-serialization-kotlinx-json:2.3.11
```

## 使用方法

- 请在多平台模块中的 build.gradle.kts 配置一下内容，请按照实际情况编写

``` kotlin
plugins {
	id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("cn.vividcode.multiplatform:ktor-client-api:2.3.11-1.0.3") 
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") 
        }
    }
}

dependencies {
    kspCommonMainMetadata("cn.vividcode.multiplatform:ktor-client-ksp:2.3.11-1.0.3")
}

tasks.withType<KotlinCompile<*>>().all {
    if (this.name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
```

## 注解介绍

### `@Api` `接口` 标记在接口上

- `名称` baseUrl `类型` String `介绍` 接口前缀路径

### `@GET` `方法` GET请求

- `名称` url `类型` String `介绍` 接口路径
- `名称` auth `类型` Boolean `默认值` false `介绍` 是否需要授权

### `@POST` `方法` POST请求

- `名称` url `类型` String `介绍` 接口路径
- `名称` auth `类型` Boolean `默认值` false `介绍` 是否需要授权

### `@PUT` `方法` PUT请求

- `名称` url `类型` String `介绍` 接口路径
- `名称` auth `类型` Boolean `默认值` false `介绍` 是否需要授权

### `@DELETE` `方法` DELETE请求

- `名称` url `类型` String `介绍` 接口路径
- `名称` auth `类型` Boolean `默认值` false `介绍` 是否需要授权

### `@Headers` `方法` 请求头

- `名称` values `类型` Array<String> `介绍` 请求头：名称:值

### `@Body` `参数` 请求体

### `@Form` `参数` 表单

- `名称` name `类型` String `介绍` 表单参数名称

### `@Query` `参数` 参数

- `名称` name `类型` String `介绍` 参数名称

### `@Path` `参数` 参数

- `名称` name `类型` String `介绍` 参数名称

### `@Header` `参数` 请求头

- `名称` name `类型` String `介绍` 请求头参数名称

### `@SHA256` `参数` 字段SHA256加密

- `名称` layer `类型` Int `默认值` 1 `介绍` 加密层数

## 定义接口文件

- 只允许使用 suspend 方法
- 支持的返回类型有 `Unit` `ResultBody<*>` `ByteArray`

定义接口

``` kotlin
@Api(baseUrl = "/test")
interface TestApi {
	
    /**
     * 通过 @Query 查询
     */
    @GET(url = "/search")
    suspend fun search(
        @Query searchKey: String,
        @Query pageSize: Int,
        @Query pageNum: Int
    ): ResultBody<*>
	
    /**
     * 通过 @Path 查询
     */
    @GET(url = "/search/{id}")
    suspend fun searchById(
    	@Path id: Int
    ): ResultBody<*>
}
```

构建后将会生成以下代码

``` kotlin
public class TestApiImpl private constructor(
    private val ktorClient: KtorClient,
) : TestApi {
    override suspend fun search(
    	searchKey: String,
    	pageSize: Int,
    	pageNum: Int,
    ): ResultBody<Any> = try {
    	val response = ktorClient.httpClient.get(urlString = "${ktorClient.domain}/test/search") {
    	    parameter("searchKey", searchKey)
    	    parameter("pageSize", pageSize)
    	    parameter("pageNum", pageNum)
    	}
    	if (response.status.isSuccess()) {
    	    response.body()
    	} else {
    	    ResultBody.failure(response.status.value, response.status.description)
    	}
    } catch (e: Exception) {
    	ResultBody.exception(e)
    }

    override suspend fun searchById(
    	id: Int,
    ): ResultBody<Any> = try {
    	val response = ktorClient.httpClient.get(urlString = "${ktorClient.domain}/test/search/${id}") {
    	
    	}
    	if (response.status.isSuccess()) {
    	    response.body()
    	} else {
    	    ResultBody.failure(response.status.value, response.status.description)
    	}
    } catch (e: Exception) {
    	ResultBody.exception(e)
    }
    
    public companion object {
    	private var instance: TestApi? = null   
    	
        public fun getInstance(ktorClient: KtorClient): TestApi = instance ?:
                TestApiImpl(ktorClient).also {
            instance = it
        }
    }
}

public val KtorClient.testApi: TestApi
    get() = TestApiImpl.getInstance(this)
```

### 接口调用方法

``` kotlin
/**
 * 配置 ktorClient，通过扩展属性获取实例
 */
val ktorClient = KtorClient.builder()
    .domain("http://localhost/api")     // 必须填写，所有请求的前缀
    .getToken { "<token>" }             // 必须填写，当注解的 auth = true 后会将token附带在请求头上
    .handleLog { }                      // 默认值：{ }
    .connectTimeout(5000L)              // 默认值：5000L
    .socketTimeout(Long.MAX_VALUE)      // 默认值：Long.MAX_VALUE
    .build()
    
/**
 * 使用 DSL 语法
 */
val ktorClient2 = ktorClient {
    domain("http://localhost/api")      // 必须填写，所有请求的前缀
    getToken { "<token>" }              // 必须填写，当注解的 auth = true 后会将token附带在请求头上
    handleLog { }                       // 默认值：{ }
    connectTimeout(5000L)               // 默认值：5000L
    socketTimeout(Long.MAX_VALUE)       // 默认值：Long.MAX_VALUE
}

/**
 * 测试组件
 */
@Composable
fun Test() {
    val coroutineScope = rememberCoroutineScope()
    val password by remember { mutableStateOf("123456") }
    var pageNum by remember { mutableIntStateOf(1) }
    Button(
        onClick = {
            coroutineScope.launch {
                val result = ktorClient.testApi.search(
                    searchKey = "<搜索内容>",
                    pageSize = 20,
                    pageNum = pageNum++
                )
                println(result)
            }
        }
    ) {
        Text("按钮")
    }
}
```