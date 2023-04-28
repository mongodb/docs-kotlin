data class Music(
    @BsonId val id: Int,
    val music: String,
    val color: String
)

val filter = Filters.eq(FoodOrder::color.name, "green")
val results = collection.find(filter).toList()
val replace = Music(1, "classical", "green")
val options = FindOneAndReplaceOptions()
    .returnDocument(ReturnDocument.AFTER)
val musicCollection = database.getCollection<Music>("example")
val result = musicCollection.findOneAndReplace(filter, replace, options)
println(result)
