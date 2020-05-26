package news.lambda.android.ui.item.detail

import android.text.Html
import android.text.format.DateUtils
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.drawBehind
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.painter.Stroke
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.ripple.ripple
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import arrow.core.Option
import arrow.core.Some
import max.Uri
import news.lambda.android.ui.item.Preview
import news.lambda.android.util.timeAgo
import news.lambda.app.component.ItemDetailComponent.Msg
import news.lambda.app.component.ItemDetailComponent.Props
import news.lambda.model.ItemId
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import news.lambda.util.exhaustive
import oolong.Dispatch

sealed class AdapterType {
    data class Header(val header: Props.Header) : AdapterType()
    data class Row(val row: Props.Row) : AdapterType()
    object Footer : AdapterType()
}

@Composable
fun ItemDetailScreen(props: Props, dispatch: Dispatch<Msg>) {
    Scaffold(
        bodyContent = {
            val header = when (val propsHeader = props.header) {
                is Some -> listOf(AdapterType.Header(propsHeader.t))
                else -> emptyList()
            }
            val footer = listOf(AdapterType.Footer)
            val data = header + props.rows.map(AdapterType::Row) + footer
            Column {
                AdapterList(data) { item ->
                    when (item) {
                        is AdapterType.Header -> Header(item.header)
                        is AdapterType.Row -> Row(item.row, dispatch)
                        is AdapterType.Footer -> Footer()
                    }.exhaustive
                }
            }
        }
    )
}

@Composable
fun Header(header: Props.Header) {
    Column {
        Preview(header.uri)
        Spacer(modifier = Modifier.size(16.dp))
        Title(header.title)
        Spacer(modifier = Modifier.size(4.dp))
        Subtitle(header.authorId, header.createdAt)
        Spacer(modifier = Modifier.size(16.dp))
        if (header.text is Some) {
            Text(header.text)
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun Footer() {
    Divider(color = Color(0xFFEAEAEA))
    Spacer(modifier = Modifier.height(16.dp))
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
        // TODO: Compose doesn't have anything like Html.fromHtml() that produces an AnnotatedString
        val html = Html.fromHtml(text.t).toString().trimEnd()
        Text(html, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
fun Row(row: Props.Row, dispatch: Dispatch<Msg>) {
    when (row) {
        is Props.Row.Id -> {
            row.load(dispatch)
            ItemLoading()
        }
        is Props.Row.Loading -> ItemLoading()
        is Props.Row.Loaded -> ItemRow(row, dispatch)
    }
}

@Composable
fun ItemLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        gravity = ContentGravity.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ItemRow(row: Props.Row.Loaded, dispatch: Dispatch<Msg>) {
    val color = Color(127, 127, 127, 0xFF)
    val insetWidth = 16
    val gutterWidth = (row.depth * insetWidth).dp
    Clickable(
        onClick = {
            if (row.collapsed) {
                row.expand(dispatch)
            } else {
                row.collapse(dispatch)
            }
        },
        modifier = Modifier.ripple()
    ) {
        Column(
            modifier = Modifier
                .drawBehind {
                    for (i in 0..row.depth) {
                        val strokeWidth = 2 * density
                        val x = i * insetWidth * density - strokeWidth / 2
                        drawLine(
                            color,
                            Offset(x, 0F),
                            Offset(x, size.height),
                            Stroke(strokeWidth)
                        )
                    }
                }
                .fillMaxWidth()
                .padding(start = gutterWidth)
        ) {
            if (row.depth == 0) {
                Divider(color = Color(0xFFEAEAEA))
            }
            Spacer(Modifier.height(8.dp))
            Subtitle(row.authorId, row.createdAt)
            Spacer(modifier = Modifier.size(4.dp))
            if (!row.collapsed) {
                Text(row.text)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
fun ItemDetailScreenPreview() {
    ItemDetailScreen(
        Props(
            Some(
                Props.Header(
                    UserId("pardom"),
                    UnixTime(System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS * 23),
                    Some("Michael Pardo's Personal Website with Links to Github, LinkedIn, and Resume"),
                    Some("Where is the unrelated ship? The mermaid is cunningly most unusual. Ellipse, starlight travel, and coordinates. Intelligent teleporters, to the radiation dome. Starships view with rumour at the modern radiation dome!"),
                    Some(Uri.parse("https://michaelpardo.com"))
                )
            ),
            setOf(
                Props.Row.Loaded(
                    0,
                    UserId("pg"),
                    UnixTime(System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS * 8),
                    Some("This turbulence has only been examined by a huge girl. Bravely gather a transporter."),
                    false,
                    {},
                    {}
                ),
                Props.Row.Loaded(
                    1,
                    UserId("pardom"),
                    UnixTime(System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS * 5),
                    Some("Transporters reproduce with modification! Strange, ordinary parasites mechanically transform a colorful, remarkable alien. Starships yell with nuclear flux!"),
                    false,
                    {},
                    {}
                ),
                Props.Row.Loaded(
                    0,
                    UserId("kensuke155"),
                    UnixTime(System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS * 2),
                    Some("All the crewmates capture evasive, human queens. Human, distant aliens pedantically fight an ordinary, reliable c-beam. Why does the planet reproduce?"),
                    true,
                    {},
                    {}
                ),
                Props.Row.Loading(1, ItemId(2))
            )
        ),
        {}
    )
}
