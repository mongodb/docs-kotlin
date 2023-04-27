try {
    val result = collection.insertMany(paintOrders)
    result.insertedIds.values
        .forEach { doc -> doc.asInt32().value }
    println("Inserted a document with the following ids: ${result.insertedIds}")
} catch(e: MongoBulkWriteException){
    e.writeResult.inserts
        .forEach { doc -> doc.id.asInt32().value }
    println(
        "A MongoBulkWriteException occurred, but there are " +
        "successfully processed documents with the following ids: ${e.writeResult}"
    )
    println(collection.find().toList().forEach { println(it) })
}
