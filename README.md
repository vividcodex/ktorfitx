# KtorfitX 3.2.1-3.0.0-Beta2

[![Maven](https://img.shields.io/badge/Maven-Central-download.svg)](https://central.sonatype.com/search?q=cn.ktorfitx:multiplatform-core)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://vividcodex.github.io/ktorfitx-document/index_md.html)
[![License](https://img.shields.io/badge/Apache-2.0-brightgreen.svg)](https://github.com/vividcodex/ktorfitx/blob/master/LICENSE-2.0)

## 更新时间

### 2025-07-08

## 项目简介

Kotlin Multiplatform 平台是为了实现类似RESTful风格的网络请求接口定义，使用代码生成实现类

Ktor Server 是为了自动生成路由层代码定义，达到无需手动管理复杂路由代码管理

## 版本说明

Kotlin `2.2.0`

Ktor `3.2.1`

KSP `2.2.0-2.0.2`

## 迁移 从 2.x 迁移至 3.x

- 修改了依赖包名

请将依赖包改为：cn.ktorfitx 下的依赖包，旧的：cn.vividcode.multiplatform 包现在已经处于暂停开发状态

- ktorfitx-api 模块拆分为 multiplatform-core 和 multiplatform-mock 模块

- 服务端

添加 Ktor Server 端支持，标记注解，符号处理器会自动生成对应的路由方法，您您只需要调用它就可以了

## 支持平台

### Kotlin Multiplatform

Android, IOS, Desktop (JVM), WasmJs, Js

### Ktor Server

Ktor Server, Ktor Auth, Ktor WebSocket

## 依赖说明

请使用和 ktorfitx 相同版本的 ktor 版本，以保证他们的最佳兼容性

### 目前所有依赖项

- cn.ktorfitx:multiplatform-core:3.2.1-3.0.0-Beta2
- cn.ktorfitx:multiplatform-annotation:3.2.1-3.0.0-Beta2
- cn.ktorfitx:multiplatform-websockets:3.2.1-3.0.0-Beta2
- cn.ktorfitx:multiplatform-mock:3.2.1-3.0.0-Beta2
- cn.ktorfitx:multiplatform-ksp:3.2.1-3.0.0-Beta2
- cn.ktorfitx:server-annotation:3.2.1-3.0.0-Beta2
- cn.ktorfitx:server-websockets:3.2.1-3.0.0-Beta2
- cn.ktorfitx:server-auth:3.2.1-3.0.0-Beta2
- cn.ktorfitx:server-ksp:3.2.1-3.0.0-Beta2
- cn.ktorfitx:common-ksp-util:3.2.1-3.0.0-Beta2

## 使用方法

### Kotlin Multiplatform

- 请在 Kotlin Multiplatform 模块中的 build.gradle.kts 配置一下内容，请按照实际情况编写

``` kotlin
plugins {
    // 可选
    alias(libs.plugins.kotlin.serialization)
    
    // 必选
    alias(libs.plugins.ksp)
}

val ktorfitxVersion = "3.2.1-3.0.0-Beta2"

kotlin {
    sourceSets {
        // ...
        commonMain.dependencies {
            // 必选
            implementation("cn.ktorfitx:multiplatform-core:ktorfitxVersion")
            implementation("cn.ktorfitx:multiplatform-annotation:ktorfitxVersion")
            
            // 可选，如果你需要 WebSocket 支持
            implementation("cn.ktorfitx:multiplatform-websockets:ktorfitxVersion")
            
            // 可选，如果你需要 Mock 支持
            implementation("cn.ktorfitx:multiplatform-mock:ktorfitxVersion")
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") 
        }
        // ...
    }
}

dependencies {
    kspCommonMainMetadata("cn.ktorfitx:multiplatform-ksp:ktorfitxVersion")
}

tasks.withType<KotlinCompilationTask<*>>().all {
    "kspCommonMainKotlinMetadata".also {
        if (name != it) dependsOn(it)
    }
}
```

### Ktor Server

``` kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.ksp)
}

group = "cn.ktorfitx.server.sample"
version = property("ktorfitx.sample.version").toString()

application { 
    mainClass = "io.ktor.server.netty.EngineMain"

    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

kotlin { 
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_2
        apiVersion = KotlinVersion.KOTLIN_2_2
        jvmTarget = JvmTarget.JVM_21 
    }
}

dependencies {
    val ktorfitxVersion = "3.2.1-3.0.0-Beta2"

    // 常规注解（必选）
    implementation("cn.ktorfitx:server-annotation:$ktorfitxVersion")
    
    // 可选，Ktor Auth 支持
    implementation("cn.ktorfitx:server-auth:$ktorfitxVersion")
    
    // 可选，Ktor WebSocket 支持
    implementation("cn.ktorfitx:server-websockets:$ktorfitxVersion")
    
    // Ktor的依赖库，需要自行定义，以上依赖仅提供注解支持
    implementation(libs.bundles.server.sample)
    
    // 代码生成扫描器（必选）
    ksp("cn.ktorfitx:server-ksp:$ktorfitxVersion")
}
```

## 编译期错误检查

支持编译期错误检查，当您使用的方式不正确时，Ktorfitx 将会在编译期提供错误检查，
以帮助用户更快的定位错误

## 注解介绍

### Kotlin Multiplatform

#### CLASS

- `@Api` 定义接口

#### FUNCTION

- `@GET` GET 请求
- `@POST` POST 请求
- `@PUT` PUT 请求
- `@DELETE` DELETE 请求
- `@PATCH` PATCH 请求
- `@OPTIONS` OPTIONS 请求
- `@HEAD` HEAD 请求
- `@BearerAuth` 启用授权
- `@Headers` 多个请求头
- `@Mock` Mock
- `@ExceptionListeners` 自定义异常监听
- `@WebSocket` WebSocket

#### VALUE_PARAMETER

- `@Body` 请求体
- `@Query` 请求参数
- `@Field` x-www-form-urlencoded 字段
- `@Part` form-data 字段
- `@Header` 动态请求头
- `@Path` path 参数

### Ktor Server

#### FILE

- `@RouteGenerator` 路由生成文件定义

#### FUNCTION

- `@GET` GET 请求
- `@POST` POST 请求
- `@PUT` PUT 请求
- `@DELETE` DELETE 请求
- `@PATCH` PATCH 请求
- `@OPTIONS` OPTIONS 请求
- `@HEAD` HEAD 请求
- `@Authentication` 路由授权
- `@WebSocket` WebSocket