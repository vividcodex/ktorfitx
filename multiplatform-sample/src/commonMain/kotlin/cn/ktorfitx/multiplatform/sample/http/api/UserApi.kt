package cn.ktorfitx.multiplatform.sample.http.api

import cn.ktorfitx.multiplatform.annotation.Api
import cn.ktorfitx.multiplatform.annotation.Mock
import cn.ktorfitx.multiplatform.annotation.POST
import cn.ktorfitx.multiplatform.annotation.Path
import cn.ktorfitx.multiplatform.mock.MockProvider
import kotlinx.serialization.Serializable

@Api(url = "system")
interface SystemApi {
	
	@POST(url = "friend/{friendId}")
	@Mock(provider = FriendMockProvider::class)
	suspend fun fetchFriend(
		@Path friendId: Int,
	): FriendDTO
}

@Serializable
data class FriendDTO(
	val friendId: Int,
)

object FriendMockProvider : MockProvider<FriendDTO> {
	override fun provide(): FriendDTO {
		TODO("")
	}
}