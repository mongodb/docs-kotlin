val existsComparison = Filters.and(Filters.exists("qty"), Filters.nin("qty", 5, 8))
val resultsFlow = collection.find(existsComparison)
resultsFlow.collect { println(it) }
