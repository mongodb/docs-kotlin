val filter = Filters.eq("_id", 1)
val update = Updates.push(PaintOrder::vendor.name, "C")
collection.updateOne(filter, update)
