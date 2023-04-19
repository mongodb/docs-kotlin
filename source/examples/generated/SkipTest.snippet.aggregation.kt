val filter = Filters.empty()
val aggregate = listOf(
    Aggregates.match(filter),
    Aggregates.sort(Sorts.descending("qty")),
    skip(5)
)
collection.aggregate<Document>(aggregate).toList().forEach { println(it.toJson()) }
