val searchStage = Aggregates.search(
    SearchOperator.compound()
        .filter(
            listOf(
                SearchOperator.`in`(fieldPath("genres"), listOf("Comedy")),
                SearchOperator.phrase(fieldPath("fullplot"), "new york"),
                SearchOperator.numberRange(fieldPath("year")).gtLt(1950, 2000),
                SearchOperator.wildcard(fieldPath("title"), "Love *")
            )
        )
)

val projectStage = Aggregates.project(
    Projections.include("title", "year", "genres", "cast"))

val pipeline = listOf(searchStage, projectStage)
val resultsFlow = collection.aggregate(pipeline)

resultsFlow.collect { println(it) }
