package news.lambda.data.service.user

import news.lambda.model.User

typealias GetUserById = suspend (id: String) -> User
