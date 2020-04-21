package news.lambda.util

import java.net.URI

val URI.tld: String
    get() = host.split('.').takeLast(2).joinToString(".")
