val filter = Filters.empty()
val results = collection.find(filter)
    .sort(descending("qty"))
    .skip(5)
    .collect { println(it) }
