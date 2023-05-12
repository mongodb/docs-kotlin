val equalComparison = Filters.eq("qty", 5)
val resultsFlow = collection.find(equalComparison)
resultsFlow.collect { println(it) }
