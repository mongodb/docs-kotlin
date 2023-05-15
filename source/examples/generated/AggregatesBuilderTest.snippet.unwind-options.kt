Aggregates.unwind(
    "\$lowest_rated_two_movies",
    UnwindOptions().preserveNullAndEmptyArrays(true)
)
