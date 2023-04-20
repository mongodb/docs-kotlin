val filter = Filters.eq("_id", 1)
val smallerFilter = Filters.lt("smaller", 15)
val options = FindOneAndUpdateOptions()
    .returnDocument(ReturnDocument.AFTER)
    .arrayFilters(listOf(smallerFilter))
val update = Updates.inc("qty.$[smaller]", 5)
val result = collection.findOneAndUpdate(filter, update, options)
collection.find().collect { println(it) }
