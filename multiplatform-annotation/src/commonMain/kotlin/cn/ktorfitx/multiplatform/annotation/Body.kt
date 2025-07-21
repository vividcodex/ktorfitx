package cn.ktorfitx.multiplatform.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Body(
	val format: SerializationFormat = SerializationFormat.JSON,
)

enum class SerializationFormat {
	JSON,
	XML,
	CBOR,
	PROTO_BUF
}