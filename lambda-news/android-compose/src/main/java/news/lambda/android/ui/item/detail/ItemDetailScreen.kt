package news.lambda.android.ui.item.detail

import android.text.format.DateUtils
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import arrow.core.Option
import arrow.core.Some
import max.Uri
import news.lambda.android.ui.item.Preview
import news.lambda.android.util.timeAgo
import news.lambda.app.component.ItemDetailComponent.Msg
import news.lambda.app.component.ItemDetailComponent.Props
import news.lambda.model.Item
import news.lambda.model.ItemId
import news.lambda.model.RemoteData.Success
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import oolong.Dispatch

@Composable
fun ItemDetailScreen(props: Props, dispatch: Dispatch<Msg>) {
    Scaffold(
        bodyContent = {
            val itemOption = props.item
            if (itemOption is Success) {
                val item = itemOption.data
                Column {
                    Preview(item.uriOption)
                    Spacer(modifier = Modifier.size(16.dp))
                    Title(item.titleOption)
                    Spacer(modifier = Modifier.size(4.dp))
                    Subtitle(item.authorId, item.createdAt)
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(item.textOption)
                    Spacer(modifier = Modifier.size(16.dp))
                    Rows(props.rows)
                }
            }
        }
    )
}

@Composable
fun Preview(uri: Option<Uri>) {
    if (uri is Some) {
        Preview(uri.t)
    }
}

@Composable
fun Title(title: Option<String>) {
    if (title is Some) {
        Text(
            title.t,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun Subtitle(author: UserId, createdAt: UnixTime) {
    Text(
        "${createdAt.timeAgo} by ${author.value}",
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.caption
    )
}

@Composable
fun Text(text: Option<String>) {
    if (text is Some) {
        Text(
            // TODO: Compose doesn't have anything like Html.fromHtml() that produces an AnnotatedString
            text.t,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun Rows(rows: Set<Props.Row>) {
    AdapterList(data = rows.toList()) { row ->
        Row(row)
    }
}

@Composable
fun Row(row: Props.Row) {

}

@Preview
@Composable
fun ItemDetailScreenPreview() {
    ItemDetailScreen(
        Props(
            Success(
                Item.Story(
                    ItemId(0),
                    UserId("pardom"),
                    UnixTime(System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS * 23),
                    emptySet(),
                    42,
                    182,
                    "Michael Pardo's Personal Website with Links to Github, LinkedIn, and Resume",
                    Some("Where is the unrelated ship? The mermaid is cunningly most unusual. Ellipse, starlight travel, and coordinates. Intelligent teleporters, to the radiation dome. Starships view with rumour at the modern radiation dome!"),
                    Some(Uri.parse("https://michaelpardo.com"))
                )
            ),
            emptySet()
        ),
        {}
    )
}
