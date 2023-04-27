try {
    val bulkOperations = listOf(
        (doc3),
        (doc4)
    )
    val bulkWrite = collection.bulkWrite(bulkOperations)
} catch (e: MongoBulkWriteException) {
    println("A MongoBulkWriteException occurred with the following message: " + e.message)
}
