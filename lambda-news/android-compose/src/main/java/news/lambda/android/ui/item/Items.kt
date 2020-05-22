package news.lambda.android.ui.item

import android.text.format.DateUtils
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
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
import news.lambda.model.Item.*
import news.lambda.model.ItemId
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import news.lambda.util.tld

@Composable
fun Item(item: Item) {
    when (item) {
        is Story -> StoryRow(item)
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
fun StoryRow_(story: Story) {
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
        ConstraintLayout(constraintSet = ConstraintSet {
            val hasUri = story.uri is Some

            val favicon = tag("favicon")
            val source = tag("source")
            val title = tag("title")
            val subtitle = tag("subtitle")
            val preview = tag("preview")

            favicon.apply {
                left constrainTo parent.left
                top constrainTo parent.top

                left.margin = 16.dp
                top.margin = 16.dp
            }

            source.apply {
                width = spread

                left constrainTo favicon.right
                top constrainTo favicon.top
                right constrainTo preview.left
                bottom constrainTo favicon.bottom

                left.margin = 4.dp
            }

            title.apply {
                width = spread

                top constrainTo if (hasUri) favicon.bottom else parent.top
                left constrainTo parent.left
                right constrainTo if (hasUri) preview.left else parent.right

                left.margin = 16.dp
                top.margin = if (hasUri) 4.dp else 16.dp
                right.margin = 16.dp
                bottom.margin = 16.dp
            }

            subtitle.apply {
                width = spread

                left constrainTo title.left
                top constrainTo title.bottom
                right constrainTo title.right
                bottom constrainTo parent.bottom

                top.margin = 4.dp
                bottom.margin = 16.dp
            }

            preview.apply {
                right constrainTo parent.right
                top constrainTo parent.top
                bottom constrainTo parent.bottom

                right.margin = 16.dp
                top.margin = 16.dp
                bottom.margin = 16.dp
            }
        }) {
            val uri = story.uri
            if (uri is Some) {
                Favicon(uri.t)
                Source(uri.t)
                Preview(uri.t)
            }
            Title(story.title)
            Subtitle(story.authorId, story.createdAt, story.descendantCount)
        }
    }
}

@Composable
fun StoryRow(story: Story) {
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Source(uri.t)
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                }
                Title(story.title)
                Spacer(modifier = Modifier.size(4.dp))
                Subtitle(story.authorId, story.createdAt, story.descendantCount)
            }
            if (uri is Some) {
                Spacer(modifier = Modifier.size(8.dp))
                Preview(uri.t)
            }
        }
    }
}

@Composable
private fun Favicon(uri: Uri) {
    val tld = uri.tld
    if (tld != null) {
        Box(modifier = Modifier.tag("favicon").size(10.dp)) {
            CoilImage(data = "https://www.google.com/s2/favicons?domain=$tld")
        }
    }
}

@Composable
private fun Source(uri: Uri) {
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
private fun Title(title: String) {
    Text(
        title,
        modifier = Modifier.tag("title"),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = MaterialTheme.typography.subtitle1
    )
}

@Composable
private fun Subtitle(author: UserId, createdAt: UnixTime, commentCount: Long) {
    Text(
        "$commentCount comments · ${author.value} · ${createdAt.timeAgo}",
        modifier = Modifier.tag("subtitle"),
        style = MaterialTheme.typography.caption
    )
}

@Composable
private fun Preview(uri: Uri) {
    val context = ContextAmbient.current
    val openChromeCustomTab = {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(context, uri.toAndroidUri())
    }
    Box(
        modifier = Modifier
            .tag("preview")
            .clip(RoundedCornerShape(4.dp))
            .height(64.dp)
            .aspectRatio(1.6F)
        ,
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
fun Job(job: Job) {
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
fun Ask(ask: Ask) {
    Text(ask.title, modifier = Modifier.preferredHeight(48.dp))
}

@Composable
fun Poll(poll: Poll) {
    Text(poll.title, modifier = Modifier.preferredHeight(48.dp))
}

@Preview
@Composable
fun StoryPreview() {
    StoryRow(
        Story(
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
