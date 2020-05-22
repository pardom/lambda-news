package news.lambda.android.ui.item

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.tooling.preview.Preview
import arrow.core.Some
import max.Uri
import news.lambda.app.component.ItemDetailComponent.Msg
import news.lambda.app.component.ItemDetailComponent.Props
import oolong.Dispatch

@Composable
fun ItemDetailScreen(props: Props, dispatch: Dispatch<Msg>) {
    Scaffold(
        topAppBar = { Header(props.header, dispatch) },
        bodyContent = { Body(props) }
    )
}

@Composable
fun Header(header: Props.Header, dispatch: Dispatch<Msg>) {
    Column {
        TopAppBar(title = { Text(header.title) })
        val uri = header.uri
        if (uri is Some) {
            Preview(uri.t)
        }
    }
}

@Composable
fun Body(props: Props) {
}

@Preview
@Composable
fun ItemDetailScreenPreview() {
    ItemDetailScreen(
        Props(
            Props.Header(
                "Michael Pardo's Personal Website with Links to Github, LinkedIn, and Resume",
                Some(Uri.parse("https://michaelpardo.com"))
            ),
            Props.Body.Loading
        ),
        {}
    )
}
