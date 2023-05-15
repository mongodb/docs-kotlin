data class Results(val year: Int)

val filter = Filters.empty()
val projection = Projections.include(YearlyTemperature::year.name)
val resultsFlow = collection.find<Results>(filter).projection(projection)
resultsFlow.collect { println(it) }
