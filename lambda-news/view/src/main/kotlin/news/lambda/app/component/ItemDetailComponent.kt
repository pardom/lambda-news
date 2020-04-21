package news.lambda.app.component

import news.lambda.data.service.item.GetItemById
import news.lambda.model.ItemId
import oolong.Init
import oolong.Update
import oolong.View
import oolong.effect.none

object ItemDetailComponent {

    // Types

    data class Model(
        val itemId: ItemId
    )

    sealed class Msg

    data class Props(
        val title: String
    )

    // MVU triplet

    val init: (ItemId) -> Init<Model, Msg> =
        { itemId ->
            {
                Model(
                    itemId = itemId
                ) to none()
            }
        }

    val update: (GetItemById) -> Update<Model, Msg> =
        { getItemById ->
            { msg, model ->
                model to none()
            }
        }

    val view: View<Model, Props> = { model ->
        Props("${model.itemId}")
    }
}
