val database = client.getDatabase("sample_mflix")
val collection: MongoCollection<Document> = database.getCollection("theaters")
val centralPark = Point(Position(-73.9667, 40.78))
val query = near("location.geo", centralPark, 10000.0, 5000.0)
val projection = fields(include("location.address.city"), excludeId())
collection.find(query)
    .projection(projection)
    .toList().forEach { println(it) }
