package news.lambda.android

import news.lambda.model.ItemId
import news.lambda.model.UserId
import java.net.URI

object Routes {

    private const val URI_BASE = "http://lambda.news"

    fun itemList(): URI = URI("$URI_BASE/items")

    fun itemDetail(itemId: ItemId): URI = URI("$URI_BASE/items/${itemId.value}")

    fun userDetail(userId: UserId): URI = URI("$URI_BASE/users/${userId.value}")
}
