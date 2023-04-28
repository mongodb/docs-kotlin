val orderBySort = orderBy(
    Sorts.descending("letter"), ascending("_id")
)
collection.find().sort(orderBySort)
