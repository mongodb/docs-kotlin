collection.createIndex(Indexes.text("food"))
val metaTextScoreSort: Bson = Sorts.metaTextScore("score")
val metaTextScoreProj: Bson = Projections.metaTextScore("score")
val searchTerm = "maple donut"
val searchQuery = Filters.text(searchTerm)

val results = collection.find(searchQuery)
    .projection(metaTextScoreProj)
    .sort(metaTextScoreSort)
results.collect { println(it) }
