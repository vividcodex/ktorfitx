# KtorfitX 3.2.1-3.0.0-Beta1

[![Maven](https://img.shields.io/badge/Maven-Central-download.svg)](https://central.sonatype.com/search?q=cn.ktorfitx:multiplatform-core)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://vividcodex.github.io/ktorfitx-document/index_md.html)
[![License](https://img.shields.io/badge/Apache-2.0-brightgreen.svg)](https://github.com/vividcodex/ktorfitx/blob/master/LICENSE-2.0)

## 更新时间

### 2025-07-05

## 项目简介

Kotlin Multiplatform 平台是为了实现类似RESTful风格的网络请求接口定义，使用代码生成实现类

Ktor Server 是为了自动生成路由层代码定义，达到无需手动管理复杂路由代码管理

## 版本说明

Kotlin `2.2.0`

Ktor `3.2.1`

KSP `2.2.0-2.0.2`

## 迁移 从 2.x 迁移至 3.x

- 修改了依赖包名

目前改为：cn.ktorfitx:multiplatform-xxx

- api 模块拆分为 core 和 mock 模块

- 服务端

添加 Ktor Server 端支持，目标是进行路由方法定义的自动生成

依赖包为：cn.ktorfitx:server-xxx

## 支持平台

### Kotlin Multiplatform

Android, IOS, Desktop (JVM), WasmJs, Js

### Ktor Server

Ktor Server, Ktor Auth, Ktor WebSocket(暂未开发)

## 依赖说明

请使用和 ktorfitx 相同版本的 ktor 版本，以保证最佳兼容性

### 目前所有依赖项

- cn.ktorfitx:multiplatform-core
- cn.ktorfitx:multiplatform-annotation
- cn.ktorfitx:multiplatform-websockets
- cn.ktorfitx:multiplatform-mock
- cn.ktorfitx:multiplatform-ksp
- cn.ktorfitx:server-annotation
- cn.ktorfitx:server-websockets (暂未开发完成)
- cn.ktorfitx:server-auth
- cn.ktorfitx:server-ksp
- cn.ktorfitx:common-ksp-util

## 使用方法

### Kotlin Multiplatform

- 请在 Kotlin Multiplatform 模块中的 build.gradle.kts 配置一下内容，请按照实际情况编写

``` kotlin
plugins {
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ksp)
}

val ktorfitxVersion = "3.2.1-3.0.0-Beta1"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("cn.ktorfitx:multiplatform-core:ktorfitxVersion")
            
            // 可选，如果你需要 WebSocket 支持
            implementation("cn.ktorfitx:multiplatform-websockets:ktorfitxVersion")
            
            // 可选，如果你需要 Mock 支持
            implementation("cn.ktorfitx:multiplatform-mock:ktorfitxVersion")
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin") 
        }
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

```kotlin
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
	val ktorfitxVersion = "3.2.1-3.0.0-Beta1"
	
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