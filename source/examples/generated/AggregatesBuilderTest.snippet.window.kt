val pastMonth = Windows.timeRange(-1, MongoTimeUnit.MONTH, Windows.Bound.CURRENT)

val resultsFlow = weatherCollection.aggregate<Document>(listOf(
       Aggregates.setWindowFields("\$localityId",
           Sorts.ascending("measurementDateTime"),
           WindowOutputFields.sum("monthlyRainfall", "\$rainfall", pastMonth),
           WindowOutputFields.avg("monthlyAvgTemp", "\$temperature", pastMonth)
       )
