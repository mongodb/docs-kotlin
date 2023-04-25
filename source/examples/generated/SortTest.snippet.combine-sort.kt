val orderBySort: Bson = orderBy(
    descending("letter"), ascending("_id")
)
collection.find().sort(orderBySort)
