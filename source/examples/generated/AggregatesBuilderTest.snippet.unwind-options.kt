Aggregates.unwind(
    "\$${Results::lowestRatedTwoMovies.name}",
    UnwindOptions().preserveNullAndEmptyArrays(true)
)
