package news.lambda.android

import max.Uri
import news.lambda.model.ItemId
import news.lambda.model.UserId

object Routes {

    private const val URI_BASE = "https://lambda.news"

    fun itemList(): Uri = Uri.parse("$URI_BASE/items")

    fun itemDetail(itemId: ItemId): Uri = Uri.parse("$URI_BASE/items/${itemId.value}")

    fun userDetail(userId: UserId): Uri = Uri.parse("$URI_BASE/users/${userId.value}")
}
