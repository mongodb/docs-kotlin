val filter = Filters.size("vendor", 3)
collection.find(filter).toList().forEach { println(it) }
