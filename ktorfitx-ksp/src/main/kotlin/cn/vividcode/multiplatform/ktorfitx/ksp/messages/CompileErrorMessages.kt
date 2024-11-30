package cn.vividcode.multiplatform.ktorfitx.ksp.messages

/**
 * 错误信息配置
 */
object CompileErrorMessages {
	
	private const val BODY_SIZE = "方法不允许在参数中使用多个 @Body 注解"
	private const val BODY_TYPE = "@Body 的类型未找到"
	
	/**
	 * `@Body` 数量错误
	 */
	fun bodySizeMessage(functionQualifiedName: String): String =
		"$functionQualifiedName $BODY_SIZE"
	
	/**
	 * `@Body` 类型错误
	 */
	fun bodyTypeMessage(functionQualifiedName: String): String =
		"$functionQualifiedName $BODY_TYPE"
}