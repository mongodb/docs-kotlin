// Replace the placeholder with your Atlas connection string
val uri = "<connection string>"

val settings = MongoClientSettings.builder()
    .applyConnectionString(ConnectionString(uri))
    .compressorList(
        listOf(
            MongoCompressor.createSnappyCompressor(),
            MongoCompressor.createZstdCompressor())
    )
    .build()

// Create a new client with your settings
val mongoClient = MongoClient.create(settings)
