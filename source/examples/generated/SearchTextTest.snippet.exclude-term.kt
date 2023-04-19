val filter = Filters.text("furious -fast")
collection.find(filter).toList().forEach { println(it) }
