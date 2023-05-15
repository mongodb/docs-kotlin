data class Results(val food: String, val color: String)

val projection = Projections.excludeId()
val filter = Filters.eq(FoodOrder::color.name, "green")
val update = Updates.set(FoodOrder::food.name, "pizza")
val options = FindOneAndUpdateOptions()
    .projection(projection)
    .upsert(true)
    .maxTime(5, TimeUnit.SECONDS)
/* The result variable contains your document in the
    state before your update operation is performed. */
val resultsCollection = database.getCollection<Results>("example")
val result = resultsCollection.findOneAndUpdate(filter, update, options)
println(result)
