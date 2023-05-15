Aggregates.group(
    "\$year",
    Accumulators.maxN(
        "highest_two_ratings",
        "\$imdb.rating",
        2
    )
)
