package cn.ktorfitx.server.sample.routes

import cn.ktorfitx.server.annotation.Cookie
import cn.ktorfitx.server.annotation.CookieEncoding
import cn.ktorfitx.server.annotation.POST

@POST("/cookie/test1")
fun cookieTest1(
	@Cookie cookie: String,
	@Cookie("custom") cookie2: String?,
	@Cookie(encoding = CookieEncoding.RAW) cookie3: String
): String = ""