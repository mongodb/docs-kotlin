collection.find().sort(Sorts.ascending("_id"))
    .toList().forEach{ println(it) }
