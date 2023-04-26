collection.find()
    .sort(ascending("length"))
    .limit(3)
    .skip(3)
    .toList().forEach { println(it) }
