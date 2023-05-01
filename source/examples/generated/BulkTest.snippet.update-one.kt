val filter = Filters.eq("_id", 2)
val update = Updates.set("x", 8)
val doc = UpdateOneModel<SampleDoc>(filter, update)
