val collection =
    database.getCollection<ExampleDataClass>("bass")
collection.drop()
