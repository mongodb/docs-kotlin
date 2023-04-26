val doc = collection.find(Filters.eq("name", "Gabriel García Márquez")).firstOrNull()
doc?.let { doc ->
    println("_id: ${doc.getObjectId("_id")}, name: ${doc.getString("name")}, dateOfDeath: ${doc.getDate("dateOfDeath")}")

    val novels = doc["novels"] as? List<BasicDBObject>
    novels?.forEach {novel ->
        println("title: ${novel.getString("title")}, yearPublished: ${novel.getInt("yearPublished")}")
    }
}
