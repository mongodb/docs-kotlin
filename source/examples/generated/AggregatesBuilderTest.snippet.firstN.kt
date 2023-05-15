Aggregates.group(
    "\$year",
    Accumulators.firstN(
        "first_two_movies",
        "\$title",
        2
    )
)
