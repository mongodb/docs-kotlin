// return all documents with only the name field
// and excludes the id
val filter = Filters.empty()
val projection = Projections.fields(
    Projections.include(FruitName::name.name),
    Projections.excludeId()
)
val flowResults = collection.find<FruitName>(filter).projection(projection)
println(flowResults.toList())
