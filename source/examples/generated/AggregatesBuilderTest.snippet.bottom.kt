Aggregates.group(
    "\$year",
    Accumulators.bottom(
        "shortest_movies",
        Sorts.descending("runtime"),
        listOf("\$title", "\$runtime")
    )
)
