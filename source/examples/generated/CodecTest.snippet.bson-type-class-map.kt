val bsonTypeClassMap = BsonTypeClassMap()
val clazz = bsonTypeClassMap[BsonType.ARRAY]
println("Java type: " + clazz.name)
