Aggregates.group(
    "\$year",
    Accumulators.topN(
        "longest_three_movies",
        Sorts.descending("runtime"),
        listOf("\$title", "\$runtime"),
        3
    )
)
