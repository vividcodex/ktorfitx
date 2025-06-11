# KtorfitX 3.1.3-2.5.0

[![Maven](https://img.shields.io/badge/Maven-Central-download.svg)](https://central.sonatype.com/search?q=cn.vividcode.multiplatform:ktorfitx-api)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://vividcodex.github.io/ktorfitx-document/index_md.html)
[![License](https://img.shields.io/badge/Apache-2.0-brightgreen.svg)](https://github.com/vividcodex/ktorfitx/blob/master/LICENSE-2.0)

## 更新时间

### 2025-06-11

## 版本说明

Kotlin `2.1.21`

Ktor `3.1.3`

KSP `2.1.21-2.0.2`

## 支持平台

Android, IOS, Desktop (JVM), WasmJs, Js

## 依赖说明

请使用和 ktorfitx 相同版本的 ktor 版本，以保证兼容性

## 使用方法

- 请在多平台模块中的 build.gradle.kts 配置一下内容，请按照实际情况编写

``` kotlin
plugins {
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val ktorfitxVersion = "3.1.3-2.5.0"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("cn.vividcode.multiplatform:ktorfitx-api:ktorfitxVersion")
            
            // 可选，如果你需要WebSocket支持
            implementation("cn.vividcode.multiplatform:ktorfitx-websockets:ktorfitxVersion")
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") 
        }
    }
}

dependencies {
    kspCommonMainMetadata("cn.vividcode.multiplatform:ktorfitx-ksp:ktorfitxVersion")
}

tasks.withType<KotlinCompilationTask<*>>().all {
    "kspCommonMainKotlinMetadata".also {
        if (name != it) dependsOn(it)
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
    
    // 多个接口作用域
    val apiScopes: Array<KClass<out ApiScope>> = [],
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

### `@WebSocket` `方法` WebSocket连接

``` kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WebSocket(
    
    // 接口路径
    val url: String
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

## 编译期错误检查

支持编译期错误检查，当您使用的方式不正确时，Ktorfitx 将会在编译期提供错误检查，
以帮助用户更快的定位错误