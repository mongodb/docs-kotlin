val ejsonStr = "{ \"_id\": { \"\$oid\": \"507f1f77bcf86cd799439011\"}," +
        "\"myNumber\": {\"\$numberLong\": \"4794261\" }}}"

val doc = Document.parse(ejsonStr)
println(doc)
