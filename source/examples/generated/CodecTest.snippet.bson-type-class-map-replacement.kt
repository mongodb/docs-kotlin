// TODO: see if can find better typing for this than `*`
val replacements = mutableMapOf<BsonType, Class<*>>(BsonType.ARRAY to MutableSet::class.java)
val bsonTypeClassMap = BsonTypeClassMap(replacements)
val clazz = bsonTypeClassMap[BsonType.ARRAY]
println("Java type: " + clazz.name)
