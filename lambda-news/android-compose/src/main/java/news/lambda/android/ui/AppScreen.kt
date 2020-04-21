package news.lambda.android.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import androidx.ui.animation.Crossfade
import androidx.ui.material.MaterialTheme
import max.Navigator
import news.lambda.app.component.AppComponent.Msg
import news.lambda.app.component.AppComponent.Props
import news.lambda.util.exhaustive
import oolong.Dispatch
import oolong.dispatch.contramap

val NavigatorAmbient = staticAmbientOf<Navigator>()

@Model
class AppModel(
    var props: Props = Props.Uninitialized,
    var dispatch: Dispatch<Msg> = {}
)

@Composable
fun AppScreen(appModel: AppModel, navigator: Navigator) {
    Providers(NavigatorAmbient provides navigator) {
        MaterialTheme(LightColors) {
            Crossfade(appModel.props.javaClass.simpleName) {
                when (val props = appModel.props) {
                    is Props.Uninitialized -> {
                    }
                    is Props.ItemList -> ItemListScreen(
                        props.props,
                        contramap(appModel.dispatch, Msg::ItemList)
                    )
                    is Props.ItemDetail -> ItemDetailScreen(
                        props.props,
                        contramap(appModel.dispatch, Msg::ItemDetail)
                    )
                }.exhaustive
            }
        }
    }
}
