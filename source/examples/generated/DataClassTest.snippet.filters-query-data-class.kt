val record = DataStorage("SSD", 120.0)
// Infixed query
DataStorage::productName.eq(record.productName)
// Nested query
val bson = eq(DataStorage::productName, record.productName)
// Kmongo DSL
val filter = DataStorage::productName eq record.productName
