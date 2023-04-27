val paintOrder = listOf(
    PaintOrder(qty = 5, color = "red"),
    PaintOrder(qty = 10, color = "purple"))
val result = collection.insertMany(paintOrder)

result.insertedIds.values
    .forEach { doc -> doc.asObjectId().value }

println("Inserted a document with the following ids: ${result.insertedIds}")
