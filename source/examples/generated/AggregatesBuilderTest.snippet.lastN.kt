Aggregates.group(
    "\$${Movie::year.name}",
    Accumulators.lastN(
        Results::lastThreeMovies.name,
        "\$${Movie::title.name}",
        3
    )
)
