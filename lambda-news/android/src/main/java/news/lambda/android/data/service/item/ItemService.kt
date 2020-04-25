package news.lambda.android.data.service.item

import com.dropbox.android.external.store4.MemoryPolicy.MemoryPolicyBuilder
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import news.lambda.android.data.service.item.api.retrofit.ItemApiService
import news.lambda.android.util.toEither
import news.lambda.data.service.item.GetItemById
import news.lambda.data.service.item.GetItemIdsByCategory
import news.lambda.model.Item
import news.lambda.model.Item.Category
import news.lambda.model.ItemId

object ItemService {

    val getItemById: (ItemApiService) -> GetItemById =
        { itemApiService ->
            val store = StoreBuilder
                .fromNonFlow(apiGetItemById(itemApiService))
                .cachePolicy(MemoryPolicyBuilder().setMemorySize(1000).build())
                .build();

            { itemId -> store.stream(StoreRequest.cached(itemId, true)).toEither() }
        }

    val getItemIdsByCategory: (ItemApiService) -> GetItemIdsByCategory =
        { itemApiService ->
            val store = StoreBuilder
                .fromNonFlow(apiGetItemIdsByCategory(itemApiService))
                .build();

            { category -> store.stream(StoreRequest.cached(category, true)).toEither() }
        }

    val apiGetItemById: (ItemApiService) -> suspend (ItemId) -> Item =
        { itemApiService ->
            { itemId -> itemApiService.getItemById(itemId.value).toItem() }
        }

    val apiGetItemIdsByCategory: (ItemApiService) -> suspend (Category) -> List<ItemId> =
        { itemApiService ->
            { category ->
                when (category) {
                    Category.TOP -> itemApiService.getTopStoryIds().map(::ItemId)
                    Category.NEW -> itemApiService.getNewStoryIds().map(::ItemId)
                    Category.ASK -> itemApiService.getAskStoryIds().map(::ItemId)
                    Category.SHOW -> itemApiService.getShowStoryIds().map(::ItemId)
                    Category.JOB -> itemApiService.getJobStoryIds().map(::ItemId)
                }
            }
        }

}
