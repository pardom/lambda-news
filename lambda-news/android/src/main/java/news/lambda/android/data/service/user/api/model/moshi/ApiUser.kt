package news.lambda.android.data.service.user.api.model.moshi

import news.lambda.model.ItemId
import news.lambda.model.UnixTime
import news.lambda.model.User
import news.lambda.model.UserId

data class ApiUser(
    val id: String,
    val created: Long,
    val karma: Long,
    val about: String,
    val submitted: List<Long>
) {

    fun toUser() = User(
        UserId(id),
        UnixTime(created),
        karma,
        about,
        submitted.map(::ItemId)
    )
}

