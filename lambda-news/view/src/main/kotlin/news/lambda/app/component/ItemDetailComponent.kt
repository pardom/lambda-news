package news.lambda.app.component

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import max.Uri
import news.lambda.data.service.item.GetItemById
import news.lambda.model.Item
import news.lambda.model.ItemId
import news.lambda.model.RemoteData
import news.lambda.model.RemoteData.Loading
import news.lambda.model.RemoteData.NotAsked
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import news.lambda.model.toRemoteData
import news.lambda.util.msgEffect
import oolong.Dispatch
import oolong.Effect
import oolong.Init
import oolong.Next
import oolong.Update
import oolong.View
import oolong.effect
import oolong.effect.none

object ItemDetailComponent {

    // Types

    data class Model(
        val itemId: ItemId,
        val items: Map<ItemId, RemoteData<Throwable, Item>>
    )

    sealed class Msg {

        // Events

        data class ItemRequested(
            val itemId: ItemId
        ) : Msg()

        // Messages

        data class SetItem(
            val itemId: ItemId,
            val item: Either<Throwable, Item>
        ) : Msg()

    }

    data class Props(
        val header: Option<Header>,
        val rows: Set<Row>
    ) {

        data class Header(
            val authorId: UserId,
            val createdAt: UnixTime,
            val title: Option<String>,
            val text: Option<String>,
            val uri: Option<Uri>
        )

        sealed class Row {

            abstract val depth: Int

            data class Id(
                override val depth: Int,
                val itemId: ItemId,
                val load: (Dispatch<Msg>) -> Unit
            ) : Row()

            data class Loading(
                override val depth: Int,
                val itemId: ItemId
            ) : Row()

            data class Failure(
                override val depth: Int,
                val itemId: ItemId
            ) : Row()

            data class Loaded(
                override val depth: Int,
                val authorId: UserId,
                val createdAt: UnixTime,
                val text: Option<String>
            ) : Row()
        }
    }

    // MVU triplet

    val init: (ItemId) -> Init<Model, Msg> =
        { itemId ->
            {
                Model(
                    itemId = itemId,
                    items = mapOf(itemId to NotAsked)
                ) to msgEffect(Msg.ItemRequested(itemId))
            }
        }

    val update: (GetItemById) -> Update<Model, Msg> =
        { getItemById ->
            val updateItemRequested = updateItemRequested(getItemById);
            { msg, model ->
                when (msg) {
                    is Msg.ItemRequested -> updateItemRequested(msg, model)
                    is Msg.SetItem -> updateSetItem(msg, model)
                }
            }
        }

    val view: View<Model, Props> =
        { model ->
            Props(
                viewHeader(model),
                viewRows(model)
            )
        }

    // Updates

    private val updateItemRequested: (GetItemById) -> (Msg.ItemRequested, Model) -> Next<Model, Msg> =
        { getItemById ->
            val getItemByIdEffect = getItemByIdEffect(getItemById);
            { msg, model ->
                val loading = model.items[msg.itemId] is Loading
                val effect = if (loading) none() else getItemByIdEffect(msg.itemId)
                model.copy(
                    items = model.items + (msg.itemId to Loading)
                ) to effect
            }
        }

    private val updateSetItem: (Msg.SetItem, Model) -> Next<Model, Msg> =
        { msg, model ->
            val items = when (val item = msg.item.toRemoteData()) {
                is RemoteData.Success -> listOf(msg.itemId to item) +
                        item.data.childIdsOption
                            .map { childIds ->
                                childIds.map { childId ->
                                    childId to model.items.getOrElse(childId) { NotAsked }
                                }
                            }
                            .getOrElse { emptySet<Pair<ItemId, RemoteData<Throwable, Item>>>() }
                else -> listOf(msg.itemId to item)
            }
            model.copy(
                items = model.items + items
            ) to none()
        }

    // Views

    private val viewHeader: (Model) -> Option<Props.Header> =
        { model ->
            when (val item = model.items.getValue(model.itemId)) {
                is RemoteData.Success -> Some(
                    Props.Header(
                        item.data.authorId,
                        item.data.createdAt,
                        item.data.titleOption,
                        item.data.textOption,
                        item.data.uriOption
                    )
                )
                else -> None
            }
        }

    private val viewRows: (Model) -> Set<Props.Row> =
        { model ->
            when (val item = model.items[model.itemId]) {
                is RemoteData.Success -> {
                    item.data
                        .childIdsOption
                        .map { itemIds ->
                            itemIds.flatMap { itemId ->
                                viewRows(0, itemId, model.items)
                            }.toSet()
                        }
                        .getOrElse { emptySet() }
                }
                else -> emptySet()
            }
        }

    private fun viewRows(
        depth: Int,
        itemId: ItemId,
        items: Map<ItemId, RemoteData<Throwable, Item>>
    ): Set<Props.Row> {
        return when (val item = items[itemId]) {
            is RemoteData.Success -> {
                setOf(viewRow(depth, itemId, item)) + item.data
                    .childIdsOption
                    .map { itemIds ->
                        itemIds.flatMap { itemId ->
                            viewRows(depth + 1, itemId, items)
                        }.toSet()
                    }
                    .getOrElse { emptySet() }
            }
            null -> emptySet()
            else -> setOf(viewRow(depth, itemId, item))
        }
    }

    private val viewRow: (Int, ItemId, RemoteData<Throwable, Item>) -> Props.Row =
        { depth, itemId, item ->
            when (item) {
                NotAsked -> Props.Row.Id(depth, itemId) { dispatch ->
                    dispatch(
                        Msg.ItemRequested(
                            itemId
                        )
                    )
                }
                Loading -> Props.Row.Loading(depth, itemId)
                is RemoteData.Failure -> Props.Row.Failure(depth, itemId)
                is RemoteData.Success -> Props.Row.Loaded(
                    depth,
                    item.data.authorId,
                    item.data.createdAt,
                    item.data.textOption
                )
            }
        }

    // Effects

    private val getItemByIdEffect: (GetItemById) -> (ItemId) -> Effect<Msg> =
        { getItemById ->
            { itemId ->
                effect { dispatch ->
                    getItemById(itemId)
                        .map { item -> Msg.SetItem(itemId, item) }
                        .collect { msg -> dispatch(msg) }
                }
            }
        }
}
