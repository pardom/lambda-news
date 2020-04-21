package news.lambda.android

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import news.lambda.android.data.service.item.ItemService
import news.lambda.android.data.service.item.api.retrofit.ItemApiService
import news.lambda.android.data.service.user.api.UserService
import news.lambda.android.data.service.user.api.retrofit.UserApiService
import news.lambda.app.component.AppComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Graph {

    // API

    val networkFlipperPlugin = NetworkFlipperPlugin()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
        .build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://hacker-news.firebaseio.com/v0/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val itemApiService: ItemApiService =
        retrofit.create(ItemApiService::class.java)

    private val userApiService: UserApiService =
        retrofit.create(UserApiService::class.java)

    // Oolong

    val init = AppComponent.init

    val update = AppComponent.update(
        ItemService.getItemById(itemApiService),
        ItemService.getItemIdsByCategory(itemApiService),
        UserService.getUserById(userApiService)
    )

    val view = AppComponent.view

}
