val filter = Filters.text("\"fate of the furious\"")
collection.find(filter).toList().forEach { println(it) }
