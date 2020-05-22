package news.lambda.app.component

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import max.Uri
import news.lambda.data.service.item.GetItemById
import news.lambda.model.Item
import news.lambda.model.ItemId
import news.lambda.model.RemoteData
import news.lambda.model.RemoteData.*
import news.lambda.model.toRemoteData
import news.lambda.util.msgEffect
import oolong.*
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
        val header: Header,
        val body: Body
    ) {

        data class Header(
            val title: String,
            val uri: Option<Uri>
        )

        sealed class Body {

            object Loading : Body()

            data class Loaded(
                val rows: List<Row>
            ) : Body()

            sealed class Row {

                abstract val depth: Int

                data class Id(
                    override val depth: Int,
                    val load: (Dispatch<Msg>) -> Unit
                ) : Row()

                data class Loading(
                    override val depth: Int,
                    val itemId: ItemId
                ) : Row()

                data class Failure(
                    override val depth: Int,
                    val load: ItemId
                ) : Row()

                data class Loaded(
                    override val depth: Int,
                    val item: Item
                ) : Row()
            }
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

    val view: View<Model, Props> = { model ->
        val parentItem = (model.items.getValue(model.itemId) as? Success)?.data
        val title = (parentItem as? Item.WithTitle)?.title ?: ""
        val uri = (parentItem as? Item.WithUri)?.uri ?: None
        Props(
            Props.Header(
                title,
                uri
            ),
            if (parentItem != null) {
                Props.Body.Loaded(
                    emptyList()
                )
            } else {
                Props.Body.Loading
            }
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
            model.copy(
                items = model.items + listOf(msg.itemId to msg.item.toRemoteData())
            ) to none()
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
