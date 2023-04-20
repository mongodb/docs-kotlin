data class PaintOrder <Document>(
    @BsonId val id: Int,
    val qty: Int,
    val color: String
)
