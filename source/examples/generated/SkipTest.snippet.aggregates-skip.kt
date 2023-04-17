collection.aggregate(listOf(Aggregates.match(filter), skip(2)))
