val results = collection.find()
    .sort(descending("length"))
    .limit(3)
    .toList()
results.forEach { println(it) }
