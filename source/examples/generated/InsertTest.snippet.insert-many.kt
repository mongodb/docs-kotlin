val paintOrder = listOf(
    PaintOrder(qty = 5, color = "red"),
    PaintOrder(qty = 10, color = "purple"))
val result = collection.insertMany(paintOrder)

val insertedIds = ArrayList<ObjectId>()
result.insertedIds.values
    .forEach { doc -> insertedIds.add(doc.asObjectId().value) }

println("Inserted a document with the following ids: $insertedIds")
