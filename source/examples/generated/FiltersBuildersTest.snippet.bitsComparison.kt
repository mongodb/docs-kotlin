val bitsComparison = bitsAllSet("a", 34)
val resultsFlow = collection.find(bitsComparison)
resultsFlow.collect { println(it) }
