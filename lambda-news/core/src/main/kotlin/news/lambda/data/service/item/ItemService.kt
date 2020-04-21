package news.lambda.data.service.item

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import news.lambda.model.Item
import news.lambda.model.Item.Category
import news.lambda.model.ItemId

typealias GetItemById = suspend (id: ItemId) -> Flow<Either<Throwable, Item>>

typealias GetItemIdsByCategory = suspend (category: Category) -> Flow<Either<Throwable, List<ItemId>>>
