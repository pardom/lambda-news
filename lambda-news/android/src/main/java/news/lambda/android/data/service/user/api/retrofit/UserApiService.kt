package news.lambda.android.data.service.user.api.retrofit

import news.lambda.android.data.service.user.api.model.moshi.ApiUser
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {

    @GET("user/{user_id}.json")
    suspend fun getUserById(
        @Path("user_id") userId: String
    ): ApiUser

}
