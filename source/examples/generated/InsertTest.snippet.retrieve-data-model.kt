data class PaintOrder(
    @BsonId val id: Int? = null,
    val qty: Int,
    val color: String
)
