package news.lambda.model

data class User(
    val id: UserId,
    val createdAt: Long,
    val karma: Long,
    val about: String,
    val itemIds: List<Long>
)
