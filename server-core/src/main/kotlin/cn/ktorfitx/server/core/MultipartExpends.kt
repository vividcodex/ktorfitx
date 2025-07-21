package cn.ktorfitx.server.core

import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray

suspend fun MultiPartData.resolve(): MultiPartParameters {
	val partDatas = mutableMapOf<String, PartData>()
	this.forEachPart { partData ->
		val name = partData.name ?: return@forEachPart
		partDatas[name] = partData
	}
	return MultiPartParameters(partDatas)
}

class MultiPartParameters(
	private val partDatas: Map<String, PartData>
) {
	
	fun getForm(name: String): PartData.FormItem {
		return partDatas[name] as? PartData.FormItem ?: error("Not Found PartItem: $name")
	}
	
	fun getFormValue(name: String): String {
		val formItem = getForm(name)
		return formItem.value
	}
	
	fun getFormOrNull(name: String): PartData.FormItem? {
		return partDatas[name] as? PartData.FormItem
	}
	
	fun getFormValueOrNull(name: String): String? {
		val formItem = getFormOrNull(name) ?: return null
		return formItem.value
	}
	
	fun getFile(name: String): PartData.FileItem {
		return partDatas[name] as? PartData.FileItem ?: error("Not Found FileItem: $name")
	}
	
	suspend fun getFileByteArray(name: String): ByteArray {
		val fileItem = getFile(name)
		return fileItem.provider().readBuffer().readByteArray()
	}
	
	fun getFileOrNull(name: String): PartData.FileItem? {
		return partDatas[name] as? PartData.FileItem
	}
	
	suspend fun getFileByteArrayOrNull(name: String): ByteArray? {
		val fileItem = getFileOrNull(name) ?: return null
		return fileItem.provider().readBuffer().readByteArray()
	}
	
	fun getBinary(name: String): PartData.BinaryItem {
		return partDatas[name] as? PartData.BinaryItem ?: error("Not Found BinaryItem: $name")
	}
	
	fun getBinaryByteArray(name: String): ByteArray {
		val binaryItem = getBinary(name)
		return binaryItem.provider().readByteArray()
	}
	
	fun getBinaryOrNull(name: String): PartData.BinaryItem? {
		return partDatas[name] as? PartData.BinaryItem
	}
	
	fun getBinaryByteArrayOrNull(name: String): ByteArray? {
		val binaryItem = getBinaryOrNull(name) ?: return null
		return binaryItem.provider().readByteArray()
	}
	
	fun getBinaryChannel(name: String): PartData.BinaryChannelItem {
		return partDatas[name] as? PartData.BinaryChannelItem ?: error("Not Found BinaryChannelItem: $name")
	}
	
	fun getBinaryChannelOrNull(name: String): PartData.BinaryChannelItem? {
		return partDatas[name] as? PartData.BinaryChannelItem
	}
	
	fun disposeAll() {
		partDatas.values.forEach { it.dispose() }
	}
}