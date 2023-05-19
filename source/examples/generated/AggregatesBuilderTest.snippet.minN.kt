Aggregates.group(
    "\$${Movie::year.name}",
    Accumulators.minN(
        Results::lowestThreeRatings.name,
        "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}",
        3
    )
)
