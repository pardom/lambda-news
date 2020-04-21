package news.lambda.app.component

import news.lambda.data.service.user.GetUserById
import news.lambda.model.UserId
import oolong.Init
import oolong.Update
import oolong.View
import oolong.effect.none

object UserDetailComponent {

    // Types

    data class Model(
        val userId: UserId
    )

    sealed class Msg

    data class Props(
        val title: String
    )

    // MVU triplet

    val init: (UserId) -> Init<Model, Msg> =
        { userId ->
            {
                Model(
                    userId = userId
                ) to none()
            }
        }

    val update: (GetUserById) -> Update<Model, Msg> =
        { getUserById ->
            { msg, model ->
                model to none()
            }
        }

    val view: View<Model, Props> = { model ->
        Props("${model.userId}")
    }
}
