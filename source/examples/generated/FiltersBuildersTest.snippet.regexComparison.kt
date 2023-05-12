val regexComparison = Filters.regex("color", "^p")
val resultsFlow = collection.find(regexComparison)
resultsFlow.collect { println(it) }
