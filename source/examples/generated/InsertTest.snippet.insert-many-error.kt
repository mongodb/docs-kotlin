val insertedIds = ArrayList<Int>()
try {
    val result = collection.insertMany(paintOrders)
    result.insertedIds.values
        .forEach { doc -> insertedIds.add(doc.asInt32().value) }
    println("Inserted a document with the following ids: $insertedIds")
} catch(exception: MongoBulkWriteException){
    exception.writeResult.inserts
        .forEach { doc -> insertedIds.add(doc.id.asInt32().value) }
    println(
        "A MongoBulkWriteException occurred, but there are " +
        "successfully processed documents with the following ids: $insertedIds"
    )
    println(collection.find().toList().forEach { println(it) })
