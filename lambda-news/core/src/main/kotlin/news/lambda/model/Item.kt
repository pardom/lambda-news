package news.lambda.model

import arrow.core.None
import arrow.core.Option
import arrow.core.toOption
import max.Uri

sealed class Item {

    enum class Category {
        TOP, NEW, ASK, SHOW, JOB
    }

    abstract val id: ItemId
    abstract val authorId: UserId
    abstract val createdAt: UnixTime

    interface WithTitle {
        val title: String
    }

    interface WithText {
        val text: Option<String>
    }

    interface WithChildIds {
        val childIds: Set<ItemId>
    }

    interface WithScore {
        val score: Long
    }

    interface WithUri {
        val uri: Option<Uri>
    }

    interface WithDescendantCount {
        val descendantCount: Long
    }

    val titleOption: Option<String>
        get() = (this as? WithTitle)?.title.toOption()

    val textOption: Option<String>
        get() = (this as? WithText)?.text ?: None

    val childIdsOption: Option<Set<ItemId>>
        get() = (this as? WithChildIds)?.childIds.toOption()

    val scoreOption: Option<Long>
        get() = (this as? WithScore)?.score.toOption()

    val uriOption: Option<Uri>
        get() = (this as? WithUri)?.uri ?: None

    val descendantCountOption: Option<Long>
        get() = (this as? WithDescendantCount)?.descendantCount.toOption()

    data class Story(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val childIds: Set<ItemId>,
        override val descendantCount: Long,
        override val score: Long,
        override val title: String,
        override val text: Option<String>,
        override val uri: Option<Uri>
    ) : Item(), WithChildIds, WithDescendantCount, WithScore, WithTitle, WithText, WithUri

    data class Comment(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val childIds: Set<ItemId>,
        override val text: Option<String>,
        val parentId: ItemId
    ) : Item(), WithChildIds, WithText

    data class Job(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val score: Long,
        override val title: String,
        override val uri: Option<Uri>
    ) : Item(), WithScore, WithTitle, WithUri

    data class Ask(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val childIds: Set<ItemId>,
        override val descendantCount: Long,
        override val score: Long,
        override val title: String,
        override val text: Option<String>,
        override val uri: Option<Uri>
    ) : Item(), WithChildIds, WithDescendantCount, WithScore, WithTitle, WithText, WithUri

    data class Poll(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val childIds: Set<ItemId>,
        override val descendantCount: Long,
        override val score: Long,
        override val title: String,
        override val text: Option<String>,
        val pollOptionIds: Set<ItemId>
    ) : Item(), WithChildIds, WithDescendantCount, WithScore, WithTitle, WithText

    data class PollOption(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val score: Long,
        override val text: Option<String>,
        val pollId: ItemId
    ) : Item(), WithScore, WithText

    data class Deleted(
        override val id: ItemId,
        override val authorId: UserId = UserId(""),
        override val createdAt: UnixTime = UnixTime(0)
    ) : Item()

}
