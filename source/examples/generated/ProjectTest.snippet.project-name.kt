// return all documents with only the name field
val filter = Filters.empty()
val projection = Projections.fields(
    Projections.include(FruitName::name.name)
)
val flowResults = collection.find<FruitName>(filter).projection(projection)
println(flowResults.toList())
