package news.lambda.android.ui.item

import android.text.format.DateUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.size
import androidx.ui.layout.width
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import arrow.core.None
import arrow.core.Some
import dev.chrisbanes.accompanist.coil.CoilImage
import max.Uri
import news.lambda.android.Routes
import news.lambda.android.ui.PlaceholderText
import news.lambda.android.ui.R.drawable
import news.lambda.android.ui.app.NavigatorAmbient
import news.lambda.android.util.timeAgo
import news.lambda.android.util.toAndroidUri
import news.lambda.model.Item
import news.lambda.model.ItemId
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import news.lambda.util.tld

@Composable
fun Item(item: Item) {
    when (item) {
        is Item.Story -> StoryRow(item)
        is Item.Job -> Job(item)
        is Item.Ask -> Ask(item)
        is Item.Poll -> Poll(item)
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
fun StoryRow(story: Item.Story) {
    val navigator = NavigatorAmbient.current
    val navigateToDetails = {
        val route = Routes.itemDetail(story.id)
        navigator.push(route)
        Unit
    }
    Clickable(
        onClick = navigateToDetails,
        modifier = Modifier.ripple().fillMaxWidth()
    ) {
        val uri = story.uri
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
                Title(story.title)
                Spacer(modifier = Modifier.size(4.dp))
                Subtitle(story.authorId, story.createdAt)
            }
            if (uri is Some) {
                Spacer(modifier = Modifier.size(8.dp))
                RoundedPreview(uri.t, Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun Favicon(uri: Uri) {
    val tld = uri.tld
    if (tld != null) {
        Box(modifier = Modifier.tag("favicon").size(10.dp)) {
            CoilImage(data = "https://www.google.com/s2/favicons?domain=$tld")
        }
    }
}

@Composable
fun Source(uri: Uri) {
    val tld = uri.tld
    if (tld != null) {
        Text(
            tld,
            modifier = Modifier.tag("source"),
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun Title(title: String) {
    Text(
        title,
        modifier = Modifier.tag("title"),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = MaterialTheme.typography.subtitle1
    )
}

@Composable
fun Subtitle(author: UserId, createdAt: UnixTime) {
    Text(
        "${createdAt.timeAgo} by ${author.value}",
        style = MaterialTheme.typography.caption
    )
}

@Composable
fun RoundedPreview(uri: Uri, modifier: Modifier = Modifier) {
    Preview(uri, modifier + Modifier.clip(RoundedCornerShape(4.dp)))
}

@Composable
fun Preview(uri: Uri, modifier: Modifier = Modifier) {
    val context = ContextAmbient.current
    val openChromeCustomTab = {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(context, uri.toAndroidUri())
    }
    Box(
        modifier = modifier + Modifier.tag("preview").aspectRatio(1.6F),
        backgroundColor = Color(0x22000000)
    ) {
        Clickable(
            onClick = openChromeCustomTab,
            modifier = Modifier
                .ripple()
                .fillMaxSize()
        ) {
            Icon(
                asset = vectorResource(id = drawable.ic_baseline_link_24),
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun Job(job: Item.Job) {
    val context = ContextAmbient.current
    val openChromeCustomTab = {
        val uri = job.uri.orNull()
        if (uri != null) {
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
                .launchUrl(context, uri.toAndroidUri())
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
                Text(uri.t.tld.orEmpty(), style = MaterialTheme.typography.caption)
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
fun Ask(ask: Item.Ask) {
    Text(ask.title, modifier = Modifier.preferredHeight(48.dp))
}

@Composable
fun Poll(poll: Item.Poll) {
    Text(poll.title, modifier = Modifier.preferredHeight(48.dp))
}

@Preview
@Composable
fun StoryPreview() {
    StoryRow(
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
