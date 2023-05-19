Aggregates.group(
    "\$${Movie::year.name}",
    Accumulators.top(
        Results::topRatedMovie.name,
        Sorts.descending("${Movie::imdb.name}.${Movie.IMDB::rating.name}"),
        listOf("\$${Movie::title.name}", "\$${Movie::imdb.name}.${Movie.IMDB::rating.name}")
    )
)
