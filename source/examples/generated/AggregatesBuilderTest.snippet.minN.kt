Aggregates.group(
    "\$year",
    Accumulators.minN(
        "lowest_three_ratings",
        "\$imdb.rating",
        3
    )
)
