package news.lambda.android.util

import android.text.format.DateUtils
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import news.lambda.model.UnixTime

val UnixTime.timeAgo: CharSequence
    get() =
        DateUtils.getRelativeTimeSpanString(
            millis,
            System.currentTimeMillis(),
            MINUTE_IN_MILLIS
        )

