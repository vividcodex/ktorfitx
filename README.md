# Ktorfitx 3.0.1-2.0.1

## 更新时间

### 2024-11-20

## 版本说明

Kotlin `2.0.21`

Ktor `3.0.1`

KSP `2.0.21-1.0.28`

## 详细文档地址

> http://vividcodex.github.io/ktorfitx-document/index_md.html

## 支持平台

Android, IOS, Desktop (JVM), WasmJs

## 依赖说明

模块中已经包含了以下依赖，不需要额外重新添加

``` kotlin
io.ktor:ktor-client-core:$ktorVersion
io.ktor:ktor-client-logging:$ktorVersion
io.ktor:ktor-client-okhttp:$ktorVersion
io.ktor:ktor-client-darwin:$ktorVersion
io.ktor:ktor-client-serialization:$ktorVersion
io.ktor:ktor-client-content-negotiation:$ktorVersion
io.ktor:ktor-serialization-kotlinx-json:$ktorVersion
```

## 使用方法

- 请在多平台模块中的 build.gradle.kts 配置一下内容，请按照实际情况编写

``` kotlin
plugins {
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val ktorVersion = "3.0.1-2.0.0"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("cn.vividcode.multiplatform:ktorfitx-api:$ktorVersion") 
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") 
        }
    }
}

dependencies {
    kspCommonMainMetadata("cn.vividcode.multiplatform:ktorfitx-ksp:$ktorVersion")
}

tasks.withType<KotlinCompile<*>>().all {
    if (this.name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
```

## 注解介绍

### `@Api` `接口` 标记在接口上

``` kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Api(
    
    // 接口前缀
    val url: String = "",
    
    // 接口作用域 
    val apiScope: KClass<out ApiScope> = DefaultApiScope::class
)
```

### `@BearerAuth` `方法` 授权

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class BearerAuth
```

### `@GET` `方法` GET请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class GET(
    
    // 接口路径
    val url: String
)
```

### `@POST` `方法` POST请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class POST(
    
    // 接口路径
    val url: String
)
```

### `@PUT` `方法` PUT请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PUT(
    
    // 接口路径
    val url: String
)
```

### `@DELETE` `方法` DELETE请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DELETE(
    
    // 接口路径
    val url: String
)
```

### `@OPTIONS` `方法` OPTIONS请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OPTIONS(
    
    // 接口路径
    val url: String
)
```

### `@PATCH` `方法` PATCH请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class PATCH(
    
    // 接口路径
    val url: String
)
```

### `@HEAD` `方法` HEAD请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class HEAD(
    
    // 接口路径
    val url: String
)
```

### `@Headers` `方法` 请求头

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Headers(
    
    // 请求头  名称:值
    val header: String,
    
    // 多个请求头  名称:值
    vararg val headers: String
)
```

### `@ExceptionListeners` `方法` 异常监听器

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ExceptionListeners(

    // 异常监听器
    val listener: KClass<out ExceptionListener<*, *>>,
    
    // 多个异常监听器
    vararg val listeners: KClass<out ExceptionListener<*, *>>
)
```

### `@Mock` `参数` Mock 本地模拟请求

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mock(
    
    // Mock 提供者
    val provider: KClass<out MockProvider<*>>,
    
    // Mock 状态
    val status: MockStatus = MockStatus.SUCCESS,
    
    // Mock 延迟
    val delayRange: LongArray = [200L]
)
```

### `@Body` `参数` Body请求体

``` kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Body
```

### `@Form` `参数` 表单

``` kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Form(

    // 表单参数名称 默认：变量名
    val name: String = ""
)
```

### `@Query` `参数` 查询参数

``` kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Query(

    // 查询参数名称 默认：变量名
    val name: String = ""
)
```

### `@Path` `参数` 路径参数

``` kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Path(

    // 路径参数名称 默认：变量名
    val name: String = ""
)
```

### `@Header` `参数` 请求头

``` kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Header(
    
    // 请求头名称 默认：变量名
    val name: String = ""
)
```

## 示例代码

### 以下为项目测试文件，比较杂乱，但是包含了绝大部分功能

``` kotlin
/**
 * TestApi
 */
package cn.vividcode.multiplatform.ktorfitx.sample.http.api

import cn.vividcode.multiplatform.ktorfitx.annotation.*
import cn.vividcode.multiplatform.ktorfitx.api.mock.MockStatus
import cn.vividcode.multiplatform.ktorfitx.api.model.ResultBody
import cn.vividcode.multiplatform.ktorfitx.sample.http.TestApiScope
import cn.vividcode.multiplatform.ktorfitx.sample.http.listener.TestResultBodyExceptionListener
import cn.vividcode.multiplatform.ktorfitx.sample.http.listener.TestUnitExceptionListener
import cn.vividcode.multiplatform.ktorfitx.sample.http.mock.ResultBodyMockProvider
import cn.vividcode.multiplatform.ktorfitx.sample.http.mock.StringMockProvider
import kotlinx.serialization.Serializable

/**
 * 项目名称：vividcode-multiplatform-ktorfit
 *
 * 作者昵称：li-jia-wei
 *
 * 创建日期：2024/8/4 23:09
 *
 * 文件介绍：TestApi
 */
@Api(url = "/test", apiScope = TestApiScope::class)
interface TestApi {
    
    /**
     * 从 3.0.1-2.0.1 开始支持定义 http:// 和 https:// 开头的API接口
     */
    @GET(url = "https://baidu.com")
    @ExceptionListeners(TestResultBodyExceptionListener::class)
    suspend fun test01(): String
    
    @POST(url = "/test02")
    @Headers("Content-Type: application/json")
    @ExceptionListeners(TestUnitExceptionListener::class)
    suspend fun test02(
        @Body testRequest: TestRequest,
        @Header testHeader: String,
    ): ResultBody<TestResponse>
    
    @BearerAuth
    @PUT(url = "/test03")
    suspend fun test03(
        @Form form1: String,
    ): ResultBody<TestResponse>
    
    @BearerAuth
    @DELETE(url = "/test04/{deleteId}")
    suspend fun test04(
        @Path deleteId: Int,
    ): ResultBody<TestResponse>
    
    @PATCH(url = "/test05")
    suspend fun test05(
        @Form form1: String,
    ): ByteArray
    
    @BearerAuth
    @OPTIONS(url = "/{name}/test06")
    suspend fun test06(
        @Path name: String,
    ): ByteArray
    
    @HEAD(url = "/test07")
    suspend fun test07()
    
    @ExceptionListeners(TestUnitExceptionListener::class)
    @GET(url = "/test08")
    suspend fun test08(): ByteArray?
    
    @GET(url = "/test09")
    suspend fun test09(): String
    
    @POST(url = "/test10")
    suspend fun test10(): String?
    
    @BearerAuth
    @Mock(ResultBodyMockProvider::class, MockStatus.SUCCESS, delayRange = [1000, 2000])
    @GET(url = "/testMock01")
    suspend fun testMock01(
        @Query param1: String,
        @Query param2: String,
    ): ResultBody<TestResponse>
    
    @Mock(ResultBodyMockProvider::class, MockStatus.EXCEPTION)
    @ExceptionListeners(TestUnitExceptionListener::class)
    @POST(url = "/testMock02")
    suspend fun testMock02(
        @Body request: TestResponse,
    ): ResultBody<TestResponse>
    
    @Mock(ResultBodyMockProvider::class)
    @PUT(url = "/testMock03")
    suspend fun testMock03(
        @Form form1: String,
    ): ResultBody<TestResponse>
    
    @Mock(ResultBodyMockProvider::class)
    @DELETE(url = "/testMock04/{deleteId}")
    suspend fun testMock04(
        @Path deleteId: Int,
    ): ResultBody<TestResponse>
    
    @Mock(StringMockProvider::class)
    @GET(url = "/testMock05")
    suspend fun testMock05(): String
    
    @Mock(StringMockProvider::class)
    @GET(url = "/testMock06")
    suspend fun testMock06(): String?
}

@Serializable
data class TestRequest(
    val param1: String,
    val param2: String,
)

@Serializable
data class TestResponse(
    val param1: String,
)

/**
 * ResultBodyMockProvider
 */
object ResultBodyMockProvider : MockProvider<ResultBody<TestResponse>> {
    
    override fun provide(status: MockStatus): ResultBody<TestResponse> {
        return when (status) {
            MockStatus.SUCCESS -> ResultBody.success(TestResponse("测试Mock 参数一"), "测试Mock 操作成功")
            MockStatus.FAILURE -> ResultBody.failure(-1, "测试Mock 操作失败")
            MockStatus.EXCEPTION -> throw TestException()
        }
    }
}

/**
 * StringMockProvider
 */
object StringMockProvider : MockProvider<String> {
    
    override fun provide(status: MockStatus): String {
        return status.toString()
    }
}

/**
 * TestUnitExceptionListener
 */
object TestUnitExceptionListener : ExceptionListener<TestException, Unit> {
    
    override fun KFunction<*>.onExceptionListener(e: TestException) {
        println(e.message!!)
    }
}

/**
 * TestResultBodyExceptionListener
 */
object TestResultBodyExceptionListener : ExceptionListener<Exception, String> {
    
    override fun KFunction<*>.onExceptionListener(e: Exception): String {
        return e.toString()
    }
}

/**
 * TestException
 */
class TestException : Exception("TestExceptionListener 异常测试")
```

### 构建后会生成实现类以及扩展调用属性

``` kotlin
public class TestApiImpl private constructor(
    private val ktorfit: KtorfitConfig,
    private val httpClient: HttpClient,
    private val mockClient: MockClient,
) : TestApi {
    override suspend fun test01(): ResultBody<String> = try {
        this.httpClient.get("${this.ktorfit.baseUrl}/test/test01").safeResultBody()
    } catch (e: Exception) {
        with(TestResultBodyExceptionListener) {
            TestApi::test01.onExceptionListener(e)
        }
    }

    override suspend fun test02(testRequest: TestRequest, testHeader: String): ResultBody<TestResponse>
            = try {
        this.httpClient.post("${this.ktorfit.baseUrl}/test/test02") {
            headers {
                append("Content-Type", "application/json")
                append("Test-Header", testHeader)
            }
            contentType(ContentType.Application.Json)
            setBody(testRequest)
        }
        .safeResultBody()
    } catch (e: TestException) {
        with(TestUnitExceptionListener) {
            TestApi::test02.onExceptionListener(e)
        }
        ResultBody.exception(e)
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun test03(form1: String): ResultBody<TestResponse> = try {
        this.httpClient.put("${this.ktorfit.baseUrl}/test/test03") {
            this@TestApiImpl.ktorfit.token?.let { bearerAuth(it()) }
            contentType(ContentType.MultiPart.FormData)
            formData {
                append("form1", form1)
            } .let {
                setBody(MultiPartFormDataContent(it))
            }
        }
        .safeResultBody()
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun test04(deleteId: Int): ResultBody<TestResponse> = try {
        this.httpClient.delete("${this.ktorfit.baseUrl}/test/test04/${deleteId}") {
            this@TestApiImpl.ktorfit.token?.let { bearerAuth(it()) }
        }
        .safeResultBody()
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun test05(form1: String): ByteArray = try {
        this.httpClient.patch("${this.ktorfit.baseUrl}/test/test05") {
            contentType(ContentType.MultiPart.FormData)
            formData {
                append("form1", form1.encrypt(EncryptType.SHA256, HexType.Lower, 1))
            } .let {
                setBody(MultiPartFormDataContent(it))
            }
        }
        .safeByteArray()
    } catch (_: Exception) {
        EmptyByteArray
    }

    override suspend fun test06(name: String): ByteArray = try {
        this.httpClient.options("${this.ktorfit.baseUrl}/test/${name.encrypt(EncryptType.SHA256,
                HexType.Lower, 1)}/test06") {
            this@TestApiImpl.ktorfit.token?.let { bearerAuth(it()) }
        }
        .safeByteArray()
    } catch (_: Exception) {
        EmptyByteArray
    }

    override suspend fun test07() {
        try {
            this.httpClient.head("${this.ktorfit.baseUrl}/test/test07")
        } catch (_: Exception) {
        }
    }

    override suspend fun test08(): ByteArray? = try {
        this.httpClient.get("${this.ktorfit.baseUrl}/test/test08").safeByteArrayOrNull()
    } catch (e: TestException) {
        with(TestUnitExceptionListener) {
            TestApi::test08.onExceptionListener(e)
        }
        null
    } catch (_: Exception) {
        null
    }

    override suspend fun test09(): String = try {
        this.httpClient.get("${this.ktorfit.baseUrl}/test/test09").safeText()
    } catch (_: Exception) {
        ""
    }

    override suspend fun test10(): String? = try {
        this.httpClient.post("${this.ktorfit.baseUrl}/test/test10").safeTextOrNull()
    } catch (_: Exception) {
        null
    }

    override suspend fun testMock01(param1: String, param2: String): ResultBody<TestResponse> = try {
        this.mockClient.get("${this.ktorfit.baseUrl}/test/testMock01", ResultBodyMockProvider,
                MockStatus.SUCCESS, 1000L..2000L) {
            this@TestApiImpl.ktorfit.token?.let { bearerAuth(it()) }
            queries {
                append("param1", param1)
                append("param2", param2.encrypt(EncryptType.SHA256, HexType.Lower, 1))
            }
        }
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun testMock02(request: TestResponse): ResultBody<TestResponse> = try {
        this.mockClient.post("${this.ktorfit.baseUrl}/test/testMock02", ResultBodyMockProvider,
                MockStatus.EXCEPTION, 200L..200L) {
            body(request)
        }
    } catch (e: TestException) {
        with(TestUnitExceptionListener) {
            TestApi::testMock02.onExceptionListener(e)
        }
        ResultBody.exception(e)
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun testMock03(form1: String): ResultBody<TestResponse> = try {
        this.mockClient.put("${this.ktorfit.baseUrl}/test/testMock03", ResultBodyMockProvider,
                MockStatus.SUCCESS, 200L..200L) {
            forms {
                append("form1", form1)
            }
        }
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun testMock04(deleteId: Int): ResultBody<TestResponse> = try {
        this.mockClient.delete("${this.ktorfit.baseUrl}/test/testMock04/${deleteId}",
                ResultBodyMockProvider, MockStatus.SUCCESS, 200L..200L)
    } catch (e: Exception) {
        ResultBody.exception(e)
    }

    override suspend fun testMock05(): String = try {
        this.mockClient.get("${this.ktorfit.baseUrl}/test/testMock05", StringMockProvider,
                MockStatus.SUCCESS, 200L..200L)
    } catch (_: Exception) {
        ""
    }

    override suspend fun testMock06(): String? = try {
        this.mockClient.get("${this.ktorfit.baseUrl}/test/testMock06", StringMockProvider,
                MockStatus.SUCCESS, 200L..200L)
    } catch (_: Exception) {
        null
    }

    public companion object {
        private var instance: TestApi? = null

        public fun getInstance(ktorClient: Ktorfit<TestApiScope>): TestApi = instance ?:
                TestApiImpl(ktorClient.ktorfit, ktorClient.httpClient, ktorClient.mockClient).also {
            instance = it
        }
    }
}

public val Ktorfit<TestApiScope>.testApi: TestApi
    get() = TestApiImpl.getInstance(this)
```

### 接口调用方法

``` kotlin
/**
 * 定义 ktorfit
 */
val testKtorfit by lazy {
    ktorfit(TestApiScope) {
        baseUrl = "http://localhost:8080/api"
        token { "<token>" }
        log {
            level = LogLevel.ALL
            logger = ::println
        }
        json {
            prettyPrint = true
            prettyPrintIndent = "\t"
        }
    }
}

/**
 * TestApiScope
 */
object TestApiScope : ApiScope {
    
    override val name: String = "测试作用域"
}

/**
 * 测试组件
 */
@Composable
fun Test() {
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                val result = testKtorfit.testApi.test01()
                println(result.data)
            }
        }
    ) {
        Text("按钮")
    }
}
```