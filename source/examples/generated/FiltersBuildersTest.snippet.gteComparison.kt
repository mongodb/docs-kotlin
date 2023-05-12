val gteComparison = Filters.gte("qty", 10)
val resultsFlow = collection.find(gteComparison)
resultsFlow.collect { println(it) }
