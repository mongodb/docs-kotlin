Aggregates.project(
    Projections.fields(
        Projections.computed(Results::rating.name, "\$${Movie::rated.name}"),
        Projections.excludeId()
    )
)
