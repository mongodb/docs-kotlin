collection.distinct(
    Movie::type.name, Filters.eq(Movie::languages.name, "French"), String::class.java
)
