Aggregates.group(
    "\$${Movie::year.name}",
    Accumulators.bottom(
        Results::shortestMovies.name,
        Sorts.descending(Movie::runtime.name),
        listOf("\$${Movie::title.name}", "\$${Movie::runtime.name}")
    )
)
