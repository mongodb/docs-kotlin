val filter = Filters.and(Filters.lte("qty", 5), Filters.ne("color", "pink"))
collection.find(filter).toList().forEach { println(it) }
