val filter = Filters.regex("color", "k$")
collection.find(filter).toList().forEach { println(it) }
