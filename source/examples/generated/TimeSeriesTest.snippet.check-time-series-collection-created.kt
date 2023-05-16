val commandResult = database.runCommand(Document("listCollections", BsonInt64(1)))

println(commandResult.toJson())
