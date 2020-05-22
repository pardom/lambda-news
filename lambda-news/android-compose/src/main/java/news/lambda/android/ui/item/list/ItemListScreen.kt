package news.lambda.android.ui.item.list

import android.text.format.DateUtils
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.layout.width
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.Surface
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.ripple
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import arrow.core.None
import arrow.core.Some
import max.Uri
import news.lambda.android.Routes
import news.lambda.android.ui.PlaceholderText
import news.lambda.android.ui.app.NavigatorAmbient
import news.lambda.android.ui.item.Favicon
import news.lambda.android.ui.item.RoundedPreview
import news.lambda.android.ui.item.Source
import news.lambda.android.ui.item.Subtitle
import news.lambda.android.ui.item.Title
import news.lambda.app.component.ItemListComponent.Msg
import news.lambda.app.component.ItemListComponent.Props
import news.lambda.model.Item
import news.lambda.model.ItemId
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import oolong.Dispatch

@Composable
fun ItemListScreen(props: Props, dispatch: Dispatch<Msg>) {
    Scaffold(
        topAppBar = {
            Header(
                props.header,
                dispatch
            )
        },
        bodyContent = {
            Body(
                props.body,
                dispatch
            )
        }
    )
}

@Composable
fun Header(header: Props.Header, dispatch: Dispatch<Msg>) {
    Surface(elevation = 4.dp) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        "λ News",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                    )
                },
                elevation = 0.dp
            )
            TabRow(
                items = header.tabs,
                selectedIndex = header.tabs.indexOfFirst(Props.Header.Tab::selected),
                contentColor = MaterialTheme.colors.secondary
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
        is Props.Body.Loaded -> ItemList(
            body.rows,
            dispatch
        )
    }
}

@Composable
fun ItemList(rows: List<Props.Body.Row>, dispatch: Dispatch<Msg>) {
    // TODO: is this a Compose bug?
    if (rows.isEmpty()) return
    AdapterList(rows) { row ->
        when (row) {
            is Props.Body.Row.Id -> Id(
                row,
                dispatch
            )
            is Props.Body.Row.Loading -> Loading()
            is Props.Body.Row.Loaded -> ItemRow(row.item)
        }
        Divider(color = Color(0xFFEAEAEA))
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
fun ItemRow(item: Item) {
    val navigator = NavigatorAmbient.current
    val navigateToDetails = {
        val route = Routes.itemDetail(item.id)
        navigator.push(route)
        Unit
    }
    Clickable(
        onClick = navigateToDetails,
        modifier = Modifier.ripple().fillMaxWidth()
    ) {
        val uri = item.uriOption
        Row(
            modifier = Modifier.padding(16.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1F)
            ) {
                if (uri is Some) {
                    Row(verticalGravity = Alignment.CenterVertically) {
                        Favicon(uri.t)
                        Spacer(modifier = Modifier.width(4.dp))
                        Source(uri.t)
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                }
                Title(item.titleOption)
                Spacer(modifier = Modifier.size(4.dp))
                Subtitle(item.authorId, item.createdAt)
            }
            if (uri is Some) {
                Spacer(modifier = Modifier.size(8.dp))
                RoundedPreview(uri.t, Modifier.height(64.dp), 4F / 3F)
            }
        }
    }
}

@Composable
fun Id(id: Props.Body.Row.Id, dispatch: Dispatch<Msg>) {
    id.load(dispatch)
    Loading()
}

////////////////////////////////////////////////////////////////////////////////
// Previews

@Preview
@Composable
fun StoryRowPreview() {
    ItemRow(
        Item.Story(
            ItemId(0),
            UserId("pardom"),
            UnixTime(System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS * 23),
            emptySet(),
            42,
            182,
            "Michael Pardo's Personal Website with Links to Github, LinkedIn, and Resume",
            None,
            Some(Uri.parse("https://michaelpardo.com"))
        )
    )
}

@Preview
@Composable
fun JobRowPreview() {
    ItemRow(
        Item.Job(
            ItemId(0),
            UserId("pardom"),
            UnixTime(System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS * 23),
            42,
            "Oolong maintainers wanted",
            Some(Uri.parse("https://github.com/oolong-kt/oolong"))
        )
    )
}
