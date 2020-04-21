package news.lambda.android.ui.user

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import news.lambda.app.component.UserDetailComponent.Msg
import news.lambda.app.component.UserDetailComponent.Props
import oolong.Dispatch

@Composable
fun UserDetailScreen(props: Props, dispatch: Dispatch<Msg>) {
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
