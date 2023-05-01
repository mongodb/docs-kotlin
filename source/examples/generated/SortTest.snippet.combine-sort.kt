val orderBySort = orderBy(
    Sorts.descending("letter"), ascending("_id")
)
val results = collection.find().sort(orderBySort)
