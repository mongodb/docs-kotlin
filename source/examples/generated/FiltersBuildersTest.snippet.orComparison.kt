val orComparison = Filters.or(
    Filters.gt("qty", 8),
    Filters.eq("color", "pink")
)
val resultsFlow = collection.find(orComparison)
resultsFlow.collect { println(it) }
