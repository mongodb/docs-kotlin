val paintOrder2 = listOf(
    PaintOrder(qty = 5, color = "red"),
    PaintOrder(qty = 10, color = "purple"))
val result = collection.insertMany(paintOrder2)

result.insertedIds.values
 .forEach { it.asObjectId().value }

println("Inserted a document with the following ids: ${result.insertedIds}")
