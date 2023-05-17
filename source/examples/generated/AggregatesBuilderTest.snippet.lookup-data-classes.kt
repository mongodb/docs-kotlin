data class Order(
    @BsonId val id: Int,
    val customerId: Int,
    val item: String,
    val ordered: Int
)
data class Inventory(
    @BsonId val id: Int,
    val stock_item: String,
    val instock: Int
)
