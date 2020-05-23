package news.lambda.app.component

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import news.lambda.app.component.ItemListComponent.Msg.CategorySelected
import news.lambda.app.component.ItemListComponent.Msg.ItemRequested
import news.lambda.app.component.ItemListComponent.Msg.SetCategoryItemIds
import news.lambda.app.component.ItemListComponent.Msg.SetItem
import news.lambda.data.service.item.GetItemById
import news.lambda.data.service.item.GetItemIdsByCategory
import news.lambda.model.Item
import news.lambda.model.Item.Category
import news.lambda.model.Item.Category.JOB
import news.lambda.model.ItemId
import news.lambda.model.RemoteData
import news.lambda.model.RemoteData.Failure
import news.lambda.model.RemoteData.Loading
import news.lambda.model.RemoteData.NotAsked
import news.lambda.model.RemoteData.Success
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

object ItemListComponent {

    // Types

    data class Model(
        val items: Map<ItemId, RemoteData<Throwable, Item>>,
        val categoryItemIds: Map<Category, List<ItemId>>,
        val selectedCategory: Category
    )

    sealed class Msg {

        // Events

        data class ItemRequested(
            val itemId: ItemId
        ) : Msg()

        data class CategorySelected(
            val category: Category
        ) : Msg()

        // Messages

        data class SetItem(
            val itemId: ItemId,
            val item: Either<Throwable, Item>
        ) : Msg()

        data class SetCategoryItemIds(
            val category: Category,
            val itemIds: Either<Throwable, List<ItemId>>
        ) : Msg()
    }

    data class Props(
        val header: Header,
        val body: Body
    ) {

        data class Header(
            val tabs: List<Tab>
        ) {

            data class Tab(
                val text: String,
                val selected: Boolean,
                val selectCategory: (Dispatch<Msg>) -> Unit
            )
        }

        sealed class Body {

            object Loading : Body()

            data class Loaded(
                val rows: List<Row>
            ) : Body()

            sealed class Row {

                data class Id(val load: (Dispatch<Msg>) -> Unit) : Row()

                data class Loading(val itemId: ItemId) : Row()

                data class Failure(val load: ItemId) : Row()

                data class Loaded(val item: Item) : Row()
            }
        }
    }

    // MVU triplet

    val init: Init<Model, Msg> =
        {
            Model(
                items = emptyMap(),
                categoryItemIds = emptyMap(),
                selectedCategory = Category.TOP
            ) to msgEffect(CategorySelected(Category.TOP))
        }

    val update: (GetItemById, GetItemIdsByCategory) -> Update<Model, Msg> =
        { getItemById, getItemIdsByCategory ->
            val updateItemRequested = updateItemRequested(getItemById)
            val updateCategoryItemIdsRequested =
                updateCategoryItemIdsRequested(getItemIdsByCategory);
            { msg, model ->
                when (msg) {
                    is ItemRequested -> updateItemRequested(msg, model)
                    is CategorySelected -> updateCategoryItemIdsRequested(msg, model)
                    is SetItem -> updateSetItem(msg, model)
                    is SetCategoryItemIds -> updateSetCategoryItemIds(msg, model)
                }
            }
        }

    val view: View<Model, Props> =
        { model ->
            Props(
                viewHeader(model),
                viewBody(model)
            )
        }

    // Updates

    private val updateItemRequested: (GetItemById) -> (ItemRequested, Model) -> Next<Model, Msg> =
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

    private val updateCategoryItemIdsRequested: (GetItemIdsByCategory) -> (CategorySelected, Model) -> Next<Model, Msg> =
        { getItemIdsByCategory ->
            val getItemIdsByCategoryEffect = getItemIdsByCategoryEffect(getItemIdsByCategory);
            { msg, model ->
                model.copy(
                    selectedCategory = msg.category
                ) to getItemIdsByCategoryEffect(msg.category)
            }
        }

    private val updateSetItem: (SetItem, Model) -> Next<Model, Msg> =
        { msg, model ->
            model.copy(
                items = model.items + listOf(msg.itemId to msg.item.toRemoteData())
            ) to none()
        }

    private val updateSetCategoryItemIds: (SetCategoryItemIds, Model) -> Next<Model, Msg> =
        { msg, model ->
            when (val itemIds = msg.itemIds) {
                is Left -> TODO()
                is Right -> {
                    model.copy(
                        items = model.items + itemIds.b
                            .subtract(model.items.keys)
                            .map { itemId -> itemId to NotAsked }
                            .toMap(),
                        categoryItemIds = model.categoryItemIds + (msg.category to itemIds.b)
                    ) to none()

                }
            }
        }

    // Views

    val viewHeader: (Model) -> Props.Header =
        { model ->
            Props.Header(
                viewTabs(model)
            )
        }

    val viewTabs: (Model) -> List<Props.Header.Tab> =
        { model ->
            Category.values().toList().map { category ->
                Props.Header.Tab(
                    viewTabText(category),
                    category == model.selectedCategory,
                    { dispatch -> dispatch(CategorySelected(category)) }
                )
            }
        }

    val viewTabText: (Category) -> String =
        { category ->
            when (category) {
                JOB -> "JOBS"
                else -> category.name
            }
        }

    val viewBody: (Model) -> Props.Body =
        { model ->
            Props.Body.Loaded(viewRows(model))
        }

    val viewRows: (Model) -> List<Props.Body.Row> =
        { model ->
            model.categoryItemIds
                .getOrElse(model.selectedCategory) { emptyList() }
                .map { itemId -> viewRow(itemId, model.items.getOrElse(itemId) { NotAsked }) }
        }

    val viewRow: (ItemId, RemoteData<Throwable, Item>) -> Props.Body.Row =
        { itemId, item ->
            when (item) {
                NotAsked -> Props.Body.Row.Id { dispatch -> dispatch(ItemRequested(itemId)) }
                Loading -> Props.Body.Row.Loading(itemId)
                is Success -> Props.Body.Row.Loaded(item.data)
                is Failure -> Props.Body.Row.Failure(itemId)
            }
        }

    // Effects

    private val getItemByIdEffect: (GetItemById) -> (ItemId) -> Effect<Msg> =
        { getItemById ->
            { itemId ->
                effect { dispatch ->
                    getItemById(itemId)
                        .map { item -> SetItem(itemId, item) }
                        .collect { msg -> dispatch(msg) }
                }
            }
        }

    private val getItemIdsByCategoryEffect: (GetItemIdsByCategory) -> (Category) -> Effect<Msg> =
        { getItemIdsByCategory ->
            { category ->
                effect { dispatch ->
                    getItemIdsByCategory(category)
                        .map { itemIds -> SetCategoryItemIds(category, itemIds) }
                        .collect { msg -> dispatch(msg) }
                }
            }
        }
}
