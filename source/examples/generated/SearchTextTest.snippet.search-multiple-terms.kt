val filter = Filters.text("fate 7")
collection.find(filter).toList().forEach { println(it) }
