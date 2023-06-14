val credential = MongoCredential.createScramSha256Credential(
    "<username>", "<authenticationDb>", "<password>".toCharArray()
)
val settings = MongoClientSettings.builder()
        .applyToClusterSettings { builder: ClusterSettings.Builder ->
            builder.hosts(
                listOf(ServerAddress("<hostname>", "<port>"))
            )
        }
        .credential(credential)
        .build()

val mongoClient = MongoClient.create(settings)
