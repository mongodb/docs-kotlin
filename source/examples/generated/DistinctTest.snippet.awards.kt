collection.distinct(
    "${Movie::awards.name}.${Movie.Awards::wins.name}", Filters.empty(), Integer::class.java
)
