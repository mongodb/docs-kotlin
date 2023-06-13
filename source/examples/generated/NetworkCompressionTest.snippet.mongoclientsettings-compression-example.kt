// Replace the placeholder with your MongoDB deployment's connection string
val uri = "<connection string>"

val settings = MongoClientSettings.builder()
    .applyConnectionString(ConnectionString(uri))
    .compressorList(
        listOf(
            MongoCompressor.createSnappyCompressor(),
            MongoCompressor.createZlibCompressor(),
            MongoCompressor.createZstdCompressor())
    )
    .build()

lateinit var higherScopedCommandResult: Document
// Create a new client with your settings
val mongoClient = MongoClient.create(settings)
