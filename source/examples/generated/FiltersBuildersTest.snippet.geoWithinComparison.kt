data class Store(
    @BsonId val id: ObjectId? = null,
    val name: String,
    val coordinates: Position
)

val square = Polygon(listOf(
    Position(0.0, 0.0),
    Position(4.0, 0.0),
    Position(4.0, 4.0),
    Position(0.0, 4.0),
    Position(0.0, 0.0)))

val geoWithinComparison = geoWithin("coordinates", square)
val resultsFlow = collection.find(geoWithinComparison)
resultsFlow.collect { println(it) }
