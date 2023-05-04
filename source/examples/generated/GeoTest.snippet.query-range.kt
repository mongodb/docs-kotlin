val longIslandTriangle = Polygon(
    listOf(
        Position(-72.0, 40.0),
        Position(-74.0, 41.0),
        Position(-72.0, 39.0),
        Position(-72.0, 40.0)
    )
)
val projection = fields(
    include("theater.location.address.city"),
    excludeId()
)
val geoWithinComparison = geoWithin("location.geo", longIslandTriangle)
val resultsFlow = collection.find<Theater>(geoWithinComparison)
    .projection(projection)
resultsFlow.collect { println(it) }
