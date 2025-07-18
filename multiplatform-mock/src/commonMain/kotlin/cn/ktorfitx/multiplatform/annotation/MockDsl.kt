package cn.ktorfitx.multiplatform.annotation

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
internal annotation class MockDsl