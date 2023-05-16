val filter = Filters.eq("_id", 1)
val update = Updates.combine(
    Updates.setOnInsert(PaintOrder::qty.name, 7),
    Updates.setOnInsert(PaintOrder::color.name, "red"),
)
collection.updateOne(filter, update, UpdateOptions().upsert(true))
