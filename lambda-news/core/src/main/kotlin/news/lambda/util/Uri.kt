package news.lambda.util

import max.Uri

val Uri.tld: String?
    get() = host?.split('.')?.takeLast(2)?.joinToString(".")
