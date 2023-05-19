val pastMonth = Windows.timeRange(-1, MongoTimeUnit.MONTH, Windows.Bound.CURRENT)

val resultsFlow = weatherCollection.aggregate<Results>(listOf(
       Aggregates.setWindowFields("\$${Weather::localityId.name}",
           Sorts.ascending("${Weather::measurementDateTime.name}"),
           WindowOutputFields.sum(Results::monthlyRainfall.name, "\$${Weather::rainfall.name}", pastMonth),
           WindowOutputFields.avg(Results::monthlyAvgTemp.name, "\$${Weather::temperature.name}", pastMonth)
       )
