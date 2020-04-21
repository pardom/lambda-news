package news.lambda.android.data.service.user.api

import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import news.lambda.android.data.service.user.api.retrofit.UserApiService
import news.lambda.android.util.toEither
import news.lambda.data.service.user.GetUserById
import news.lambda.model.User
import news.lambda.model.UserId

object UserService {

    val getUserById: (UserApiService) -> GetUserById =
        { userApiService ->
            val store = StoreBuilder
                .fromNonFlow(apiGetUserById(userApiService))
                .build();

            { userId -> store.stream(StoreRequest.cached(userId, true)).toEither() }
        }

    val apiGetUserById: (UserApiService) -> suspend (UserId) -> User =
        { userApiService ->
            { userId -> userApiService.getUserById(userId.value).toUser() }
        }

}
