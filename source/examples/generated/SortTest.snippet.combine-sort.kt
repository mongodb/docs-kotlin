val orderBySort = orderBy(
    Sorts.descending(FoodOrder::letter.name), ascending("_id")
)
val results = collection.find().sort(orderBySort)
results.collect {println(it) }
