val collection = database.getCollection<FirstName>("names")
val indexInformation = collection.listIndexes().toList().first()
println(indexInformation.toJson())
