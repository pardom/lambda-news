package news.lambda.android.util

import max.Uri
import android.net.Uri as AndroidUri

fun Uri.toAndroidUri(): AndroidUri =
    AndroidUri.parse(toString())
