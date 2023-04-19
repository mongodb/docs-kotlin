val filter = Filters.empty()
collection.find(filter)
    .sort(descending("qty"))
    .skip(5)
    .toList().forEach { println(it) }
