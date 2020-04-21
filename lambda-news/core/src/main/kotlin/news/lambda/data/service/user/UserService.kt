package news.lambda.data.service.user

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import news.lambda.model.User
import news.lambda.model.UserId

typealias GetUserById = suspend (id: UserId) -> Flow<Either<Throwable, User>>
