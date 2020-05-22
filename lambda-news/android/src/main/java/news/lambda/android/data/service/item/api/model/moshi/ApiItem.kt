package news.lambda.android.data.service.item.api.model.moshi

import arrow.core.toOption
import max.Uri
import news.lambda.model.Item
import news.lambda.model.ItemId
import news.lambda.model.UnixTime
import news.lambda.model.UserId

data class ApiItem(
    val id: Long,
    val deleted: Boolean?,
    val type: Type,
    val by: String,
    val time: Long,
    val text: String?,
    val dead: Boolean?,
    val parent: Long?,
    val poll: Long?,
    val kids: Set<Long>?,
    val url: String?,
    val score: Long?,
    val title: String?,
    val parts: Set<Long>?,
    val descendants: Long?
) {

    enum class Type {
        job, story, comment, poll, pollopt
    }

    fun toItem(): Item = when (type) {
        Type.job -> toJob()
        Type.story -> toStory()
        Type.comment -> toComment()
        Type.poll -> toPoll()
        Type.pollopt -> toPollOpt()
    }

    private fun toJob() = Item.Job(
        ItemId(id),
        UserId(by),
        UnixTime(time * 1000),
        requireNotNull(score),
        requireNotNull(title),
        url.toOption().map((Uri)::parse)
    )

    private fun toStory() = Item.Story(
        ItemId(id),
        UserId(by),
        UnixTime(time * 1000),
        kids.orEmpty().map(::ItemId).toSet(),
        requireNotNull(descendants),
        requireNotNull(score),
        requireNotNull(title),
        text.toOption(),
        url.toOption().map((Uri)::parse)
    )

    private fun toComment() = Item.Comment(
        ItemId(id),
        UserId(by),
        UnixTime(time * 1000),
        kids.orEmpty().map(::ItemId).toSet(),
        requireNotNull(text).toOption(),
        requireNotNull(parent)
    )

    private fun toPoll() = Item.Poll(
        ItemId(id),
        UserId(by),
        UnixTime(time * 1000),
        kids.orEmpty().map(::ItemId).toSet(),
        requireNotNull(descendants),
        requireNotNull(score),
        requireNotNull(title),
        requireNotNull(text).toOption(),
        parts.orEmpty().map(::ItemId).toSet()
    )

    private fun toPollOpt() = Item.PollOption(
        ItemId(id),
        UserId(by),
        UnixTime(time * 1000),
        requireNotNull(score),
        requireNotNull(text).toOption(),
        ItemId(requireNotNull(parent))
    )
}
