val filter = Filters.empty()
val emptyQuery = listOf(
    Aggregates.match(filter),
    Aggregates.sort(descending("qty")),
    skip(9)
)
collection.aggregate<Document>(emptyQuery).toList().forEach { println(it.toJson()) }
