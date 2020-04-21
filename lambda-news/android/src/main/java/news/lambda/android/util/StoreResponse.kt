package news.lambda.android.util

import arrow.core.Left
import arrow.core.Right
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.StoreResponse.Data
import com.dropbox.android.external.store4.StoreResponse.Error
import com.dropbox.android.external.store4.StoreResponse.Loading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

fun <T> Flow<StoreResponse<T>>.toEither() = this
    .filter { it !is Loading }
    .map { response ->
        when (response) {
            is Loading -> error("Unreachable code")
            is Data -> Right(response.value)
            is Error -> Left(response.error)
        }
    }
