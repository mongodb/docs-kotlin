Aggregates.group(
    "\$year",
    Accumulators.lastN(
        "last_three_movies",
        "\$title",
        3
    )
)
