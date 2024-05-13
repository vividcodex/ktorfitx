# Kotlin 多平台 KtorClient 代码生成器

## 版本说明

ktor版本-代码生成器版本：例如：`2.3.11`-`1.0.1`

Kotlin：1.9.23

Ktor：2.3.11

## 最新版本

`2.3.11`-`1.0.1`

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
            implementation("cn.vividcode.multiplatform:ktor-client-api:2.3.11-1.0.1") 
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") 
        }
    }
}

dependencies {
    kspCommonMainMetadata("cn.vividcode.multiplatform:ktor-client-ksp:2.3.11-1.0.1")
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

### `@Header` `参数` 请求头

- `名称` name `类型` String `介绍` 请求头参数名称

### `@SHA256` `参数` 字段SHA256加密

- `名称` layer `类型` Int `默认值` 1 `介绍` 加密层数

## 定义接口文件

- 只允许使用 suspend 方法
- 支持的返回类型有 `Unit` `ResultBody<*>` `ByteArray`

``` kotlin
@Api(baseUrl = "/auth")
interface TestApi {
    
    /**
     * 测试 @Form 和 @Query
     */
    @GET(url = "/formAndQuery")
    suspend fun testFormAndQuery(
        @Query("queryName") queryName: String,
        @Form("formName") formName: String
    ): ResultBody<*>
    
    /**
     * 测试 Unit 返回类型 注：Unit 可以不写
     */
    @POST(url = "/testUnit")
    suspend fun testUnit(): Unit
    
    /**
     * 测试 ByteArray 返回类型
     */
    @GET(url = "/byteArray")
    suspend fun testByteArray(
        @Query("id") id: Int
    ): ByteArray
    
    /**
     * 测试 @SHA256
     */
    @GET(url = "sha256")
    suspend fun testSHA256(
        @Query("password") @SHA256(layer = 2) password: String
    ): ResultBody<*>
    
    /**
     * 测试 @Header 和 @Headers
     */
    @PUT(url = "/headerAndHeaders")
    @Headers("headersName: <headersValue>")
    suspend fun testHeaderAndHeaders(
        @Header("headerName") headerValue: String
    ): ResultBody<*>
}
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
    Button(
        onClick = {
            coroutineScope.launch {
                val result = ktorClient.testApi.testSHA256(password)
                println(result)
            }
        }
    ) {
        Text("按钮")
    }
}
```