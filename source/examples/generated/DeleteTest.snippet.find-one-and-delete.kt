val filter = Filters.eq("color", "purple")
println(collection.findOneAndDelete(filter))
