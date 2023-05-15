Aggregates.group(
    "\$year",
    Accumulators.bottom(
        "lowest_rated_two_movies",
        Sorts.descending("imdb.rating"),
        listOf("\$title", "\$imdb.rating")
    )
)
