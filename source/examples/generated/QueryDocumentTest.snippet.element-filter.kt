val filter = Filters.exists("rating")
collection.find(filter).toList().forEach { println(it) }
