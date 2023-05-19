Aggregates.group(
    "\$${Movie::year.name}",
    Accumulators.firstN(
        Results::firstTwoMovies.name,
        "\$${Movie::title.name}",
        2
    )
)
