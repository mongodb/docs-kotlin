val filter = Filters.eq("_id", 1)
val update = Updates.push("qty", 17)
val options = FindOneAndUpdateOptions()
    .returnDocument(ReturnDocument.AFTER)
val result = collection.findOneAndUpdate(filter, update, options)
collection.find().collect { println(it) }
