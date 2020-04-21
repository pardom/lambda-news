package news.lambda.android.ui.item

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import news.lambda.app.component.ItemDetailComponent.Msg
import news.lambda.app.component.ItemDetailComponent.Props
import oolong.Dispatch

@Composable
fun ItemDetailScreen(props: Props, dispatch: Dispatch<Msg>) {
    Scaffold(
        topAppBar = { Header(props, dispatch) },
        bodyContent = { Body(props) }
    )
}

@Composable
fun Header(props: Props, dispatch: Dispatch<Msg>) {
    TopAppBar(title = { Text(props.title) })
}

@Composable
fun Body(props: Props) {
}
