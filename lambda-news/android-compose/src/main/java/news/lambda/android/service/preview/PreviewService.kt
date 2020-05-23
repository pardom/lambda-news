package news.lambda.android.service.preview

import arrow.core.Option
import arrow.core.Some
import max.Uri

object PreviewService {

    suspend fun getPreviewImage(uri: Uri): Option<Uri> {
        return Some(Uri.parse("https://i.imgur.com/iXvsz2v.png"))
    }
}