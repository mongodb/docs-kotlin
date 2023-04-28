collection.find().sort(Sorts.ascending("_id"))
    .collect { println(it) }
