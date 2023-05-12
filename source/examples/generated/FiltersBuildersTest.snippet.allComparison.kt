val search = listOf("A", "D")
val allComparison = Filters.all("vendor", search)
val resultsFlow = collection.find(allComparison)
resultsFlow.collect { println(it) }
