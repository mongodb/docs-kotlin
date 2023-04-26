collection.find()
    .sort(descending("length"))
    .limit(3)
    .skip(3)
    .toList().forEach { println(it) }
