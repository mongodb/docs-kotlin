val pipeline = listOf(BsonDocument.parse("{ \$changeStreamSplitLargeEvent: {} }"))

val job = launch {
    val changeStream = collection.watch(pipeline)
    changeStream.collect {
        println("Received a change event: $it")
    }
}
