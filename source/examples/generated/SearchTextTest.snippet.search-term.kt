val filter = Filters.text("fast")
collection.find(filter).toList().forEach { println(it) }
