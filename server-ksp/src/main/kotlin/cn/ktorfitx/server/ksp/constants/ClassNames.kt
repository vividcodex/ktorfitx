package cn.ktorfitx.server.ksp.constants

import com.squareup.kotlinpoet.ClassName

internal object ClassNames {
	
	val requestMethodClassNames by lazy {
		arrayOf(GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS)
	}
	
	val GET = ClassName.bestGuess("cn.ktorfitx.server.annotation.GET")
	
	val POST = ClassName.bestGuess("cn.ktorfitx.server.annotation.POST")
	
	val PUT = ClassName.bestGuess("cn.ktorfitx.server.annotation.PUT")
	
	val DELETE = ClassName.bestGuess("cn.ktorfitx.server.annotation.DELETE")
	
	val PATCH = ClassName.bestGuess("cn.ktorfitx.server.annotation.PATCH")
	
	val HEAD = ClassName.bestGuess("cn.ktorfitx.server.annotation.HEAD")
	
	val OPTIONS = ClassName.bestGuess("cn.ktorfitx.server.annotation.OPTIONS")
	
	val Authentication = ClassName.bestGuess("cn.ktorfitx.server.annotation.Authentication")
	
	val RouteGenerator = ClassName.bestGuess("cn.ktorfitx.server.annotation.RouteGenerator")
	
	val RoutingContext = ClassName.bestGuess("io.ktor.server.routing.RoutingContext")
	
	val Routing = ClassName.bestGuess("io.ktor.server.routing.Routing")
}