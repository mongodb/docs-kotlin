val paintOrder = PaintOrder(qty = 5, color = "red")
val result = collection.insertOne(paintOrder)

val insertedId= result.insertedId.asObjectId().value
println(
    "Inserted a document with the following id: $insertedId"
)
