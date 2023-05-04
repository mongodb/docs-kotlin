val database = client.getDatabase("sample_mflix")
val collection = database.getCollection<TheaterResults>("theaters")
val centralPark = Point(Position(-73.9667, 40.78))
val query = near("location.geo", centralPark, 10000.0, 5000.0)
val projection = fields(include("location.address.city"), excludeId())
val resultsFlow = collection.find(query)
    .projection(projection)
resultsFlow.collect { println(it) }
