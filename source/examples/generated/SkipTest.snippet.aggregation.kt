val filter = Filters.empty()
val aggregate = listOf(
    Aggregates.match(filter),
    Aggregates.sort(descending("qty")),
    skip(5)
)
collection.aggregate(aggregate).collect { println(it) }
