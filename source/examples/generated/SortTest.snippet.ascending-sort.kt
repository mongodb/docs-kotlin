val results: List<Document> = ArrayList()
collection.find().sort(ascending("_id"))
for (result in results) {
    println(result.toJson())
}
