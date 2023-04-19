val filter = Filters.empty()
collection.aggregate(listOf(Aggregates.match(filter), skip(2)))
