val results = collection.find()
    .sort(descending("length"))
    .limit(3)
    .skip(3)
results.collect { println(it) }
