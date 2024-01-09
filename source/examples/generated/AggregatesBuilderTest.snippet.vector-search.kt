Aggregates.vectorSearch(
    SearchPath.fieldPath("plot_embedding"),
    listOf(-0.0072121937, -0.030757688, -0.012945653),
    "mflix_movies_embedding_index",
    2.toLong(),
    1.toLong(),
    vectorSearchOptions().filter(Filters.gte(Movie::year.name, 2016))
),
Aggregates.project(Projections.metaVectorSearchScore("vectorSearchScore"))
