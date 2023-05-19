Aggregates.group(
    "\$${Movie::year.name}",
    Accumulators.maxN(
        Results::highestTwoRatings.name,
        "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}",
        2
    )
)
