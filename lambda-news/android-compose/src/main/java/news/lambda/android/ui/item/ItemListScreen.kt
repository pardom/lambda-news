package news.lambda.android.ui.item

import androidx.compose.Composable
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.Surface
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import androidx.ui.material.TopAppBar
import androidx.ui.unit.dp
import news.lambda.app.component.ItemListComponent.Msg
import news.lambda.app.component.ItemListComponent.Props
import oolong.Dispatch

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
        when (row) {
            is Props.Body.Row.Id -> Id(row, dispatch)
            is Props.Body.Row.Loading -> Loading()
            is Props.Body.Row.Loaded -> Item(row.item)
        }
        Divider(color = Color(0xFFEAEAEA))
    }
}

@Composable
fun Id(id: Props.Body.Row.Id, dispatch: Dispatch<Msg>) {
    id.load(dispatch)
    Loading()
}
