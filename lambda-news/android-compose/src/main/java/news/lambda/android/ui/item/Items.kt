package news.lambda.android.ui.item

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.onCommit
import androidx.compose.setValue
import androidx.compose.stateFor
import androidx.ui.core.ContentScale
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.size
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import max.Uri
import news.lambda.android.service.preview.PreviewService
import news.lambda.android.ui.R.drawable
import news.lambda.android.util.timeAgo
import news.lambda.android.util.toAndroidUri
import news.lambda.model.UnixTime
import news.lambda.model.UserId
import news.lambda.util.tld

@Composable
fun Favicon(uri: Uri) {
    val tld = uri.tld
    if (tld != null) {
        Box(modifier = Modifier.size(10.dp)) {
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
            modifier = Modifier,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun Title(title: Option<String>) {
    if (title is Some) {
        Text(
            title.t,
            modifier = Modifier,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
fun Subtitle(author: UserId, createdAt: UnixTime) {
    Text(
        "${createdAt.timeAgo} by ${author.value}",
        style = MaterialTheme.typography.caption
    )
}

@Composable
fun RoundedPreview(uri: Uri, modifier: Modifier = Modifier, aspectRatio: Float = 1.6F) {
    Preview(uri, modifier + Modifier.clip(RoundedCornerShape(4.dp)), aspectRatio)
}

@Composable
fun Preview(uri: Uri, modifier: Modifier = Modifier, aspectRatio: Float = 1.6F) {
    val context = ContextAmbient.current
    val openChromeCustomTab = {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(context, uri.toAndroidUri())
    }
    Box(
        modifier = modifier + Modifier.aspectRatio(aspectRatio),
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
            val previewUri = UnfurlPreviewImage(uri)
            if (previewUri is Some) {
                CoilImageWithCrossfade(
                    previewUri.t.toString(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun UnfurlPreviewImage(uri: Uri): Option<Uri> {
    var result by stateFor<Option<Uri>>(uri) { None }
    onCommit(uri) {
        val job = CoroutineScope(Dispatchers.Main).launch {
            result = PreviewService.getPreviewImage(uri)
        }
        onDispose { job.cancel() }
    }
    return result
}
