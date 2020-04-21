package news.lambda.model

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import news.lambda.model.RemoteData.Failure
import news.lambda.model.RemoteData.Loading
import news.lambda.model.RemoteData.NotAsked
import news.lambda.model.RemoteData.Success

sealed class RemoteData<out E : Any, out A : Any> {
    object NotAsked : RemoteData<Nothing, Nothing>()
    object Loading : RemoteData<Nothing, Nothing>()
    data class Failure<out E : Any>(val error: E) : RemoteData<E, Nothing>()
    data class Success<out A : Any>(val data: A) : RemoteData<Nothing, A>()
}

inline fun <E : Any, A : Any, B : Any> RemoteData<E, A>.map(f: (A) -> B): RemoteData<E, B> {
    return when (this) {
        NotAsked -> NotAsked
        Loading -> Loading
        is Failure -> this
        is Success -> Success(f(data))
    }
}

fun <A : Any, B : Any> Either<A, B>.toRemoteData(): RemoteData<A, B> {
    return when (this) {
        is Left -> Failure(a)
        is Right -> Success(b)
    }
}
