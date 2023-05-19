Aggregates.unwind(
    "\$${Results::lowestRatedTwoMovies.name}",
    UnwindOptions().includeArrayIndex("position")
)
