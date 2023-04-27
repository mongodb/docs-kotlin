try {
    val result = collection.insertMany(paintOrders)
    println("Inserted documents with the following ids: ${result.insertedIds}")
} catch(e: MongoBulkWriteException){
    val insertedIds = e.writeResult.inserts.map { it.id.asInt32().value }
    println(
        "A MongoBulkWriteException occurred, but there are " +
        "successfully processed documents with the following ids: $insertedIds"
    )
    println(collection.find().toList().forEach { println(it) })
}
