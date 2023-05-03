val collOptions: ValidationOptions = ValidationOptions().validator(
    Filters.or(Filters.exists("commander"), Filters.exists("first officer"))
)
database.createCollection(
    "ships",
    CreateCollectionOptions().validationOptions(collOptions)
)
