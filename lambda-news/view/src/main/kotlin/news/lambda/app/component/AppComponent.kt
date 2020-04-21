package news.lambda.app.component

import arrow.core.identity
import news.lambda.data.service.item.GetItemById
import news.lambda.data.service.item.GetItemIdsByCategory
import news.lambda.data.service.user.GetUserById
import news.lambda.model.ItemId
import news.lambda.model.UserId
import oolong.Init
import oolong.Next
import oolong.Update
import oolong.View
import oolong.effect.none
import oolong.next.bimap

object AppComponent {

    // Types

    data class Model(
        val savedStates: Map<String, Screen>,
        val screen: Screen
    ) {

        sealed class Screen {
            data class ItemList(val model: ItemListComponent.Model) : Screen()
            data class ItemDetail(val model: ItemDetailComponent.Model) : Screen()
            data class UserDetail(val model: UserDetailComponent.Model) : Screen()
        }
    }

    sealed class Msg {

        // Events

        data class SetRoute(
            val route: Route,
            val lastRouteKey: String,
            val nextRouteKey: String,
            val direction: Route.Direction
        ) : Msg()

        // Delegates

        data class ItemList(val msg: ItemListComponent.Msg) : Msg()
        data class ItemDetail(val msg: ItemDetailComponent.Msg) : Msg()
        data class UserDetail(val msg: UserDetailComponent.Msg) : Msg()
    }

    sealed class Props {

        object Uninitialized : Props()

        // Delegates

        data class ItemList(val props: ItemListComponent.Props) : Props()
        data class ItemDetail(val props: ItemDetailComponent.Props) : Props()
        data class UserDetail(val props: UserDetailComponent.Props) : Props()
    }

    sealed class Route {

        enum class Direction {
            FORWARD, BACKWARD, REPLACE
        }

        object ItemList : Route()
        data class ItemDetail(val itemId: ItemId) : Route()
        data class UserDetail(val userId: UserId) : Route()
    }

    // MVU triplet

    val init: (Route) -> Init<Model, Msg> =
        { route -> { mapRouteInit(route) } }

    val update: (GetItemById, GetItemIdsByCategory, GetUserById) -> Update<Model, Msg> =
        { getItemById, getItemIdsByCategory, getUserById ->
            val updateItemList = updateItemList(getItemById, getItemIdsByCategory)
            val updateItemDetail = updateItemDetail(getItemById)
            val updateUserDetail = updateUserDetail(getUserById);
            { msg, model ->
                when (msg) {
                    is Msg.SetRoute -> mapRouteUpdate(msg, model)
                    is Msg.ItemList -> map(model, updateItemList(msg, model))
                    is Msg.ItemDetail -> map(model, updateItemDetail(msg, model))
                    is Msg.UserDetail -> map(model, updateUserDetail(msg, model))
                }
            }
        }

    val view: View<Model, Props> =
        { model ->
            when (model.screen) {
                is Model.Screen.ItemList -> Props.ItemList(ItemListComponent.view(model.screen.model))
                is Model.Screen.ItemDetail -> Props.ItemDetail(ItemDetailComponent.view(model.screen.model))
                is Model.Screen.UserDetail -> Props.UserDetail(UserDetailComponent.view(model.screen.model))
            }
        }

    // Updates

    private val updateItemList: (GetItemById, GetItemIdsByCategory) -> (Msg.ItemList, Model) -> Next<Model.Screen, Msg> =
        { getItemById, getItemIdsByCategory ->
            val itemListUpdate = ItemListComponent.update(getItemById, getItemIdsByCategory);
            { msg, model ->
                when (model.screen) {
                    is Model.Screen.ItemList -> bimap(
                        itemListUpdate(msg.msg, model.screen.model),
                        Model.Screen::ItemList,
                        Msg::ItemList
                    )
                    else -> model.screen to none()
                }
            }
        }

    private val updateItemDetail: (GetItemById) -> (Msg.ItemDetail, Model) -> Next<Model.Screen, Msg> =
        { getItemById ->
            val itemDetailUpdate = ItemDetailComponent.update(getItemById);
            { msg, model ->
                when (model.screen) {
                    is Model.Screen.ItemDetail -> bimap(
                        itemDetailUpdate(msg.msg, model.screen.model),
                        Model.Screen::ItemDetail,
                        Msg::ItemDetail
                    )
                    else -> model.screen to none()
                }
            }
        }

    private val updateUserDetail: (GetUserById) -> (Msg.UserDetail, Model) -> Next<Model.Screen, Msg> =
        { getUserById ->
            val userDetailUpdate = UserDetailComponent.update(getUserById);
            { msg, model ->
                when (model.screen) {
                    is Model.Screen.UserDetail -> bimap(
                        userDetailUpdate(msg.msg, model.screen.model),
                        Model.Screen::UserDetail,
                        Msg::UserDetail
                    )
                    else -> model.screen to none()
                }
            }
        }

    private val map: (Model, Next<Model.Screen, Msg>) -> Next<Model, Msg> =
        { model, next ->
            bimap(next, { model.copy(screen = next.first) }, ::identity)
        }

    // Routes

    private val mapRouteUpdate: (Msg.SetRoute, Model) -> Next<Model, Msg> =
        { msg, model ->
            val savedScreen: Model.Screen? = when (msg.direction) {
                Route.Direction.BACKWARD -> model.savedStates[msg.lastRouteKey]
                else -> null
            }
            if (savedScreen != null) {
                model.copy(
                    savedStates = model.savedStates - msg.lastRouteKey,
                    screen = savedScreen
                ) to none()
            } else {
                val (screen, effect) = mapRouteToScreen(msg.route)
                model.copy(
                    savedStates = model.savedStates + (msg.nextRouteKey to model.screen),
                    screen = screen
                ) to effect
            }
        }

    private val mapRouteInit: (Route) -> Next<Model, Msg> =
        { route ->
            val (screen, effect) = mapRouteToScreen(route)
            Model(emptyMap(), screen) to effect
        }

    private val mapRouteToScreen: (Route) -> Next<Model.Screen, Msg> =
        { route ->
            when (route) {
                is Route.ItemList -> bimap(
                    ItemListComponent.init(),
                    Model.Screen::ItemList,
                    Msg::ItemList
                )
                is Route.ItemDetail -> bimap(
                    ItemDetailComponent.init(route.itemId)(),
                    Model.Screen::ItemDetail,
                    Msg::ItemDetail
                )
                is Route.UserDetail -> bimap(
                    UserDetailComponent.init(route.userId)(),
                    Model.Screen::UserDetail,
                    Msg::UserDetail
                )
            }
        }
}


