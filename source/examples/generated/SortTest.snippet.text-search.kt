collection.createIndex(Indexes.text("food"))
val metaTextScoreSort = Sorts.metaTextScore("score")
val metaTextScoreProj = Projections.metaTextScore("score")
val searchTerm = "maple donut"
val searchQuery = Filters.text(searchTerm)

val results = collection.find<FoodOrderScore>(searchQuery)
    .projection(metaTextScoreProj)
    .sort(metaTextScoreSort)

results.collect { println(it) }
