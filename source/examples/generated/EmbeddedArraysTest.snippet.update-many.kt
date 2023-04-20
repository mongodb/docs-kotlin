val filter = Filters.eq("qty", 18)
val update = Updates.inc("qty.$", -3)
val options = FindOneAndUpdateOptions()
    .returnDocument(ReturnDocument.AFTER)
val result = collection.findOneAndUpdate(filter, update, options)
collection.find().collect { println(it) }
