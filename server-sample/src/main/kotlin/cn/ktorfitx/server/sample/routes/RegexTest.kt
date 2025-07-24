package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.GET
import cn.ktorfitx.server.annotation.Regex

@Regex
@GET("regex/test1/[0-9]+")
fun regexTest1(): String = "OK"

@Regex(RegexOption.UNIX_LINES)
@GET("*[")
fun regexTest2(): String = "OK"

@Regex(RegexOption.UNIX_LINES, RegexOption.LITERAL)
@GET("regex/test3/[0-9]+")
fun regexTest3(): String = "OK"