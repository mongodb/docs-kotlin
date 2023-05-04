try {
    val resultCreateIndex = theatersCollection.createIndex(Indexes.geo2dsphere("location.geo"))
    println("Index created: $resultCreateIndex")
} catch (e: MongoCommandException) {
    if (e.errorCodeName == "IndexOptionsConflict")
        println("there's an existing text index with different options")
}
