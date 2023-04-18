val filter = Filters.gt("qty", 7)
collection.find(filter).toList().forEach { println(it) }
