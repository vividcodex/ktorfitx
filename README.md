# Kotlin 多平台 KtorClient 代码生成器

## 版本说明

Kotlin `2.0.10`

Ktor `2.3.12`

KSP `2.0.10-1.0.24`

## 项目迁移

原项目由于包名规范性问题更名为：cn.vividcode.multiplatform.ktorfit

依赖迁移：

`2.3.12-1.3.2` 及以下使用的是 `cn.vividcode.multiplatform:ktor-client-api/ktor-client-ksp`

`2.3.12-1.4.0` 及以后使用的是 `cn.vividcode.multiplatform:ktorfit-api/ktorfit-ksp`

## 最新版本

项目版本 `2.3.12-1.4.0`
示例版本 `1.0.0`

## 依赖说明

模块中已经包含了以下依赖，不需要额外重新添加

``` kotlin
io.ktor:ktor-client-core:2.3.12
io.ktor:ktor-client-logging:2.3.12
io.ktor:ktor-client-cio:2.3.12
io.ktor:ktor-client-serialization:2.3.12
io.ktor:ktor-client-content-negotiation:2.3.12
io.ktor:ktor-serialization-kotlinx-json:2.3.12
```

## 使用方法

- 请在多平台模块中的 build.gradle.kts 配置一下内容，请按照实际情况编写

``` kotlin
plugins {
    id("com.google.devtools.ksp")
}

val version = "2.3.12-1.3.2"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("cn.vividcode.multiplatform:ktorfit-api:$version") 
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") 
        }
    }
}

dependencies {
    kspCommonMainMetadata("cn.vividcode.multiplatform:ktorfit-ksp:$version")
}

tasks.withType<KotlinCompile<*>>().all {
    if (this.name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
```

## 注解介绍

### `@Api` `接口` 标记在接口上

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Api(
	
	// 接口前缀
	val url: String = "",
	
	// 接口作用域
	val apiScope: KClass<out ApiScope> = ApiScope::class
)
```

### `@GET` `方法` GET请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class GET(
	
	// 接口路径
	val url: String,
	
	// 是否授权
	val auth: Boolean = false
)
```

### `@POST` `方法` POST请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class POST(
	
	// 接口路径
	val url: String,
	
	// 是否授权
	val auth: Boolean = false
)
```

### `@PUT` `方法` PUT请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PUT(
	
	// 接口路径
	val url: String,
	
	// 是否授权
	val auth: Boolean = false
)
```

### `@DELETE` `方法` DELETE请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DELETE(
	
	// 接口路径
	val url: String,
	
	// 是否授权
	val auth: Boolean = false
)
```

### `@OPTIONS` `方法` OPTIONS请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OPTIONS(
	
	// 接口路径
	val url: String,
	
	// 是否授权
	val auth: Boolean = false
)
```

### `@PATCH` `方法` PATCH请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PATCH(
	
	// 接口路径
	val url: String,
	
	// 是否授权
	val auth: Boolean = false
)
```

### `@HEAD` `方法` HEAD请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class HEAD(
	
	// 接口路径
	val url: String,
	
	// 是否授权
	val auth: Boolean = false
)
```

### `@Headers` `方法` 请求头

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Headers(
	
	// 至少一个请求头  名称:值
	val header: String,
	
	// 多个请求头  名称:值
	vararg val headers: String
)
```

### `@Mock` `参数` Mock 本地模拟请求

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
	
	// Mock 名称
	val name: String = "DEFAULT"
)
```

### `@Body` `参数` Body请求体

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Body
```

### `@Form` `参数` 表单

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Form(

    // 表单参数名称 默认：变量名
    val name: String = ""
)
```

### `@Query` `参数` 查询参数

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(

    // 查询参数名称 默认：变量名
    val name: String = ""
)
```

### `@Path` `参数` 路径参数

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Path(

    // 路径参数名称 默认：变量名
    val name: String = ""
)
```

### `@Header` `参数` 请求头

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Header(
	
	// 请求头名称 默认：变量名
	val name: String = ""
)
```

### `@Encrypt` `参数` 加密

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Encrypt(
	
	// 加密类型 默认：SHA256
	val encryptType: EncryptType = EncryptType.SHA256,
	
	// 加密层数 默认：1
	val layer: Int = 1
)
```

## 异常处理

### `Catch` catch 语句异常捕获

```kotlin
fun interface Catch<E : Exception> {
	
	fun run(e: E)
}
```

1. 泛型定义为需要捕获的异常，不允许使用 * 投影类型
2. Catch 在方法参数中使用
3. 可以定义多个，按照定义顺序执行

### `Finally` finally 语句

```kotlin
fun interface Finally {
	
	fun run()
}
```

1. Finally 在方法参数中使用
2. 可以定义多个，按照定义顺序执行

## 定义接口文件

- 只允许使用 suspend 方法
- 支持的返回类型有 `Unit` `ResultBody<T>` `ByteArray`

定义接口

``` kotlin
@Api(url = "/test", apiScope = TestApiScope::class)
interface TestApi {
	
    /**
     * 通过 @Query 查询
     */
    @GET(url = "/search")
    suspend fun search(
        @Query searchKey: String,
        @Query pageSize: Int,
        @Query pageNum: Int
    ): ResultBody<Unit>
	
    /**
     * 通过 @Path 查询
     */
    @GET(url = "/search/{id}")
    suspend fun searchById(
        @Path id: Int
    ): ResultBody<Unit>
    
    /**
     * 测试Mock
     */
    @Mock
    @POST(url = "/mock")
    suspend fun testMock(@Form name: String, @Form test: String): ResultBody<Unit>
}
```

构建后将会生成以下代码

``` kotlin
public class TestApiImpl private constructor(
    private val ktorConfig: KtorConfig,
    private val httpClient: HttpClient,
    private val mockClient: MockClient,
) : TestApi {
    override suspend fun search(
        searchKey: String,
        pageSize: Int,
        pageNum: Int,
    ): ResultBody<Unit> = try {
        val response = this.httpClient.get("${this.ktorConfig.baseUrl}/test/search") {
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

    override suspend fun searchById(id: Int): ResultBody<Unit> = try {
        val response = this.httpClient.get("${this.ktorConfig.baseUrl}/test/search/${id}")
        if (response.status.isSuccess()) {
            response.body()
        } else {
            ResultBody.failure(response.status.value, response.status.description)
        }
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun testMock(name: String, test: String): ResultBody<Unit> {
        val url = "/test/mock"
        val mockName = "DEFAULT"
        return this.mockClient.post(url, mockName) {
            forms {
                append("name", name)
                append("test", test)
            }
        }
    }

    public companion object {
        private var instance: TestApi? = null

        public fun getInstance(
            ktorConfig: KtorConfig,
            httpClient: HttpClient,
            mockClient: MockClient,
        ): TestApi = instance ?: TestApiImpl(ktorConfig, httpClient, mockClient).also {
            instance = it
        }
    }
}

public val KtorClient<TestApiScope>.testApi: TestApi
    get() = TestApiImpl.getInstance(ktorConfig, httpClient, mockClient)

```

### 接口调用方法

``` kotlin
/**
 * 配置 ktorClient，通过扩展属性获取实例
 */
val ktorClient = KtorClient.builder<TestApiScope>()
    .domain("http://localhost/api")     // 必须填写，所有请求的前缀
    .getToken { "<token>" }             // 必须填写，当注解的 auth = true 后会将token附带在请求头上
    .handleLog { }                      // 默认值：{ }
    .connectTimeout(5000L)              // 默认值：5000L
    .socketTimeout(Long.MAX_VALUE)      // 默认值：Long.MAX_VALUE
    .mocks {
        group("/testMock/test1") {
            mock(name = "<MockName>") {
                mock = success(LoginVO("test"))
                delay = (1000..2000).mockDelay
            }
        }
    }
    .build()
    
/**
 * 使用 DSL 语法
 */
val ktorClientDsl = ktorClient<TestApiScope> {
    domain("http://127.0.0.1/api")      // 必须填写，所有请求的前缀
    // 或使用分别构建方法
    domain {
        host = "127.0.0.1"
        port = 80
        safe = false
        prefix = "/api"
    }
    getToken { "<token>" }              // 必须填写，当注解的 auth = true 后会将token附带在请求头上
    handleLog { }                       // 默认值：{ }
    connectTimeout(5000L)               // 默认值：5000L
    socketTimeout(Long.MAX_VALUE)       // 默认值：Long.MAX_VALUE
    mocks {
        group("/testMock/test1") {
            mock(name = "<MockName>") {
                mock = success(LoginVO("test"))
                delay = (1000..2000).mockDelay
            }
        }
    }
}

/**
 * 测试组件
 */
@Composable
fun Test() {
    val coroutineScope = rememberCoroutineScope()
    var pageNum by remember { mutableIntStateOf(1) }
    Button(
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                val result = ktorClient.testApi.search(
                    searchKey = "<搜索内容>",
                    pageSize = 20,
                    pageNum = pageNum++
                )
            }
        }
    ) {
        Text("按钮")
    }
}
```