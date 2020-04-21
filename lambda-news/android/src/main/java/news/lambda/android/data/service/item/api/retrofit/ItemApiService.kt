package news.lambda.android.data.service.item.api.retrofit

import news.lambda.android.data.service.item.api.model.moshi.ApiItem
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemApiService {

    @GET("item/{item_id}.json")
    suspend fun getItemById(
        @Path("item_id") itemId: Long
    ): ApiItem

    @GET("topstories.json")
    suspend fun getTopStoryIds(): List<Long>

    @GET("newstories.json")
    suspend fun getNewStoryIds(): List<Long>

    @GET("askstories.json")
    suspend fun getAskStoryIds(): List<Long>

    @GET("showstories.json")
    suspend fun getShowStoryIds(): List<Long>

    @GET("jobstories.json")
    suspend fun getJobStoryIds(): List<Long>

}
