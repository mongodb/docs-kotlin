val rating = current().getField("rating")

listOf(project(fields(
    computed("numericalRating", rating
        .isNumberOr(of(1)))
)))
