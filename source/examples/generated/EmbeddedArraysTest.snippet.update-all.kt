val filter = Filters.eq("_id", 1)
val update = Updates.mul("qty.$[]", 2)
val options = FindOneAndUpdateOptions()
    .returnDocument(ReturnDocument.AFTER)
val result = collection.findOneAndUpdate(filter, update, options)
collection.find().collect { println(it) }
