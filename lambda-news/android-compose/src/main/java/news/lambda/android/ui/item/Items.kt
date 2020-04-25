package news.lambda.android.ui.item

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
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
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import arrow.core.Some
import news.lambda.android.Routes
import news.lambda.android.ui.LightColors
import news.lambda.android.ui.PlaceholderText
import news.lambda.android.ui.R.drawable
import news.lambda.android.ui.app.NavigatorAmbient
import news.lambda.android.util.timeAgo
import news.lambda.model.Item
import news.lambda.model.Item.Ask
import news.lambda.model.Item.Job
import news.lambda.model.Item.Poll
import news.lambda.model.Item.Story
import news.lambda.util.tld

@Composable
fun Item(item: Item) {
    when (item) {
        is Story -> Story(item)
        is Job -> Job(item)
        is Ask -> Ask(item)
        is Poll -> Poll(item)
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
fun Story(story: Story) {
    val context = ContextAmbient.current
    val openChromeCustomTab = {
        val uri = story.uri.orNull()
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
        navigator.push(Routes.itemDetail(story.id))
        Unit
    }
    Clickable(
        onClick = openChromeCustomTab,
        modifier = Modifier.ripple()
    ) {
        Row {
            Column(
                modifier = Modifier.weight(1F)
                        + Modifier.padding(16.dp, 8.dp, 0.dp, 8.dp)
            ) {
                val uri = story.uri
                if (uri is Some) {
                    Text(uri.t.tld, style = MaterialTheme.typography.caption)
                }
                Text(story.title, style = MaterialTheme.typography.subtitle1)
                Text(
                    "${story.descendantCount} comments · ${story.authorId.value} · ${story.createdAt.timeAgo}",
                    style = MaterialTheme.typography.caption
                )
            }
            Clickable(
                onClick = navigateToDetails,
                modifier = Modifier.ripple()
                        + Modifier.fillMaxHeight()
                        + Modifier.padding(16.dp)
            ) {
                Icon(
                    asset = vectorResource(id = drawable.ic_baseline_comment_24),
                    modifier = Modifier.fillMaxHeight(),
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun Job(job: Job) {
    val context = ContextAmbient.current
    val openChromeCustomTab = {
        val uri = job.uri.orNull()
        if (uri != null) {
            CustomTabsIntent.Builder()
                .setToolbarColor(LightColors.primary.toArgb())
                .setShowTitle(true)
                .build()
                .launchUrl(context, Uri.parse(uri.toString()))
        }
    }
    Clickable(
        onClick = openChromeCustomTab,
        modifier = Modifier.ripple()
                + Modifier.fillMaxWidth()
                + Modifier.padding(16.dp, 8.dp, 0.dp, 8.dp)
    ) {
        Column {
            val uri = job.uri
            if (uri is Some) {
                Text(uri.t.tld, style = MaterialTheme.typography.caption)
            }
            Text(job.title, style = MaterialTheme.typography.subtitle1)
            Text(
                "${job.createdAt.timeAgo}",
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun Ask(ask: Ask) {
    Text(ask.title, modifier = Modifier.preferredHeight(48.dp))
}

@Composable
fun Poll(poll: Poll) {
    Text(poll.title, modifier = Modifier.preferredHeight(48.dp))
}

@Preview
@Composable
fun LoadingPreview() {
    Loading()
}
