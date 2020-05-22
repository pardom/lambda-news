package news.lambda.model

import arrow.core.Option
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

    interface WithDescendants {
        val descendantCount: Long
    }

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
    ) : Item(), WithChildIds, WithDescendants, WithScore, WithTitle, WithText, WithUri

    data class Comment(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val childIds: Set<ItemId>,
        override val text: Option<String>,
        val parentId: Long
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
    ) : Item(), WithChildIds, WithDescendants, WithScore, WithTitle, WithText, WithUri

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
    ) : Item(), WithChildIds, WithDescendants, WithScore, WithTitle, WithText

    data class PollOption(
        override val id: ItemId,
        override val authorId: UserId,
        override val createdAt: UnixTime,
        override val score: Long,
        override val text: Option<String>,
        val pollId: ItemId
    ) : Item(), WithScore, WithText

}
