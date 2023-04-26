val books = listOf(
    Document().append("_id", 1).append("title", "The Brothers Karamazov").append("length", 824)
        .append("author", "Dostoyevsky"),
    Document().append("_id", 2)
        .append("title", "Les Misérables").append("length", 1462).append("author", "Hugo"),
    Document().append("_id", 3)
        .append("title", "Atlas Shrugged").append("length", 1088).append("author", "Rand"),
    Document().append("_id", 4)
        .append("title", "Infinite Jest").append("length", 1104).append("author", "Wallace"),
    Document().append("_id", 5)
        .append("title", "Cryptonomicon").append("length", 918).append("author", "Stephenson"),
    Document().append("_id", 6)
        .append("title", "A Dance with Dragons").append("length", 1104)
        .append("author", "Martin")
)
collection.insertMany(books)
