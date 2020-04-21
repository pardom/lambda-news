package news.lambda.util

import oolong.Effect
import oolong.effect

fun <Msg : Any> msgEffect(msg: Msg): Effect<Msg> =
    msgEffect { msg }

fun <Msg : Any> msgEffect(block: () -> Msg): Effect<Msg> =
    effect { dispatch -> dispatch(block()) }
