val loginContext = LoginContext("<LoginModule implementation from JAAS config>")
loginContext.login()
val subject: Subject = loginContext.subject

val credential = MongoCredential.createGSSAPICredential(<username>)
    .withMechanismProperty(MongoCredential.JAVA_SUBJECT_KEY, subject)

val settings = MongoClientSettings.builder()
        .applyToClusterSettings { builder ->
            builder.hosts(listOf(ServerAddress("<hostname>", "<port>")))
        }
        .credential(credential)
        .build()

val mongoClient = MongoClients.create(settings)
