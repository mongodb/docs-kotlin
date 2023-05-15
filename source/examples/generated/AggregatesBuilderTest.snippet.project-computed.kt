Aggregates.project(
    Projections.fields(
        Projections.computed("rating", "\$rated"),
        Projections.excludeId()
    )
)
