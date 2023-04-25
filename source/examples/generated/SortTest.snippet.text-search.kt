val results: List<Document> = ArrayList()

collection.createIndex(Indexes.text("food"))
val metaTextScoreSort: Bson = Sorts.metaTextScore("score")
val metaTextScoreProj: Bson = Projections.metaTextScore("score")
val searchTerm = "maple donut"
val searchQuery = Filters.text(searchTerm)

collection.find(searchQuery)
    .projection(metaTextScoreProj)
    .sort(metaTextScoreSort)
for (result in results) {
    println(result)
}
