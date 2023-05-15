Aggregates.group(
    "\$year",
    Accumulators.top(
        "top_rated_movie",
        Sorts.descending("imdb.rating"),
        listOf("\$title", "\$imdb.rating")
    )
)
