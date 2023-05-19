val results = collection.find()
    .collation(Collation.builder().locale("de@collation=phonebook").build())
    .sort(Sorts.ascending(FirstName::firstName.name))
    .toList()
