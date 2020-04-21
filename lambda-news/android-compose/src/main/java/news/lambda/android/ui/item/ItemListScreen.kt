package news.lambda.android.ui.item

import android.net.Uri
import android.text.format.DateUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.Surface
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.ripple
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import arrow.core.Option
import arrow.core.Some
import news.lambda.android.Routes
import news.lambda.android.ui.LightColors
import news.lambda.android.ui.PlaceholderText
import news.lambda.android.ui.R
import news.lambda.android.ui.app.NavigatorAmbient
import news.lambda.android.util.timeAgo
import news.lambda.app.component.ItemListComponent.Msg
import news.lambda.app.component.ItemListComponent.Props
import news.lambda.model.Item
import news.lambda.model.Item.Ask
import news.lambda.model.Item.Job
import news.lambda.model.Item.Poll
import news.lambda.model.Item.Story
import news.lambda.model.ItemId
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import news.lambda.util.tld
import oolong.Dispatch
import java.net.URI

@Composable
fun ItemListScreen(props: Props, dispatch: Dispatch<Msg>) {
    Scaffold(
        topAppBar = { Header(props.header, dispatch) },
        bodyContent = { Body(props.body, dispatch) }
    )
}

@Composable
fun Header(header: Props.Header, dispatch: Dispatch<Msg>) {
    Surface(elevation = 4.dp) {
        Column {
            TopAppBar(
                title = { Text("λ News") },
                elevation = 0.dp
            )
            TabRow(
                items = header.tabs,
                selectedIndex = header.tabs.indexOfFirst(Props.Header.Tab::selected),
                contentColor = MaterialTheme.colors.secondary,
                divider = {}
            ) { _, tab ->
                Tab(
                    text = { Text(tab.text) },
                    selected = tab.selected,
                    onSelected = { tab.selectCategory(dispatch) },
                    activeColor = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
fun Body(body: Props.Body, dispatch: Dispatch<Msg>) {
    when (body) {
        Props.Body.Loading -> TODO()
        is Props.Body.Loaded -> ItemList(body.rows, dispatch)
    }
}

@Composable
fun ItemList(rows: List<Props.Body.Row>, dispatch: Dispatch<Msg>) {
    // TODO: is this a Compose bug?
    if (rows.isEmpty()) return
    AdapterList(rows) { row ->
        Column {
            when (row) {
                is Props.Body.Row.Id -> {
                    row.load(dispatch)
                    Loading()
                }
                is Props.Body.Row.Loading -> {
                    Loading()
                }
                is Props.Body.Row.Failure -> {
                    Text("Failure")
                }
                is Props.Body.Row.Loaded -> {
                    Item(row.item, dispatch)
                }
            }
            Divider(color = Color(0xFFEAEAEA))
        }
    }
}

@Composable
fun Item(item: Item, dispatch: Dispatch<Msg>) {
    when (item) {
        is Story -> Item(
            item.id,
            item.title,
            item.uri.map(URI::tld),
            item.uri,
            item.descendantCount,
            item.authorId,
            item.createdAt
        )
        is Job -> Job(item, dispatch)
        is Ask -> Ask(item, dispatch)
        is Poll -> Poll(item, dispatch)
    }
}

@Composable
fun Loading() {
    Column(modifier = Modifier.fillMaxWidth() + Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp)) {
        PlaceholderText("", modifier = Modifier.fillMaxWidth())
        PlaceholderText("michaelpardo.com")
        PlaceholderText("34 comments - pardom - 2 hours ago")
    }
}

@Composable
fun Item(
    itemId: ItemId,
    title: String,
    subtitle: Option<String>,
    uri: Option<URI>,
    descendants: Long,
    author: UserId,
    date: UnixTime
) {
    val context = ContextAmbient.current
    val openChromeCustomTab = {
        val uri = uri.orNull()
        if (uri != null) {
            CustomTabsIntent.Builder()
                .setToolbarColor(LightColors.primary.toArgb())
                .setShowTitle(true)
                .build()
                .launchUrl(context, Uri.parse(uri.toString()))
        }
    }
    val navigator = NavigatorAmbient.current
    val navigateToDetails = {
        navigator.push(Routes.itemDetail(itemId))
        Unit
    }
    Row {
        Clickable(
            onClick = openChromeCustomTab,
            modifier = Modifier.weight(1F) + Modifier.padding(16.dp, 8.dp, 0.dp, 8.dp)
        ) {
            Column {
                if (subtitle is Some) {
                    Text(subtitle.t, style = MaterialTheme.typography.caption)
                }
                Text(title, style = MaterialTheme.typography.subtitle1)
                Text(
                    "$descendants comments · ${author.value} · ${date.timeAgo}",
                    style = MaterialTheme.typography.caption
                )
            }
        }
        Clickable(
            onClick = navigateToDetails,
            modifier = Modifier.ripple()
                    + Modifier.fillMaxHeight()
                    + Modifier.padding(16.dp)
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_baseline_comment_24),
                modifier = Modifier.fillMaxHeight(),
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun Job(job: Job, dispatch: Dispatch<Msg>) {
    Text(job.title, modifier = Modifier.preferredHeight(48.dp))
}

@Composable
fun Ask(ask: Ask, dispatch: Dispatch<Msg>) {
    Text(ask.title, modifier = Modifier.preferredHeight(48.dp))
}

@Composable
fun Poll(poll: Poll, dispatch: Dispatch<Msg>) {
    Text(poll.title, modifier = Modifier.preferredHeight(48.dp))
}

@Preview
@Composable
fun LoadingPreview() {
    Loading()
}

@Preview
@Composable
fun ItemPreview() {
    Item(
        ItemId(0),
        "Title",
        Some("michaelpardo.com"),
        Some(URI("michaelpardo.com")),
        34,
        UserId("pardom"),
        UnixTime(System.currentTimeMillis() - 3 * DateUtils.HOUR_IN_MILLIS)
    )
}
