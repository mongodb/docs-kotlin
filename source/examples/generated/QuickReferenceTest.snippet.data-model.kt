data class Movie(
    val title: String,
    val year: Int,
    val rated: String? = "Not Rated",
    val imdb: Imdb? = null,
    val genres: List<String>? = listOf()
) {
    data class Imdb(
        val rating: Double,
        val votes: Int,
        val id: Int
    )
}
