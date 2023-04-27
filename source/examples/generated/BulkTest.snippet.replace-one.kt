val filter = Filters.eq("_id", 1)
val insert = SampleDoc(1, 4)
val doc3 = ReplaceOneModel(filter, insert)
