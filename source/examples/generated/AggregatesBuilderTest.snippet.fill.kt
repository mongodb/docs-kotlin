val resultsFlow = weatherCollection.aggregate<Weather>(listOf(
    Aggregates.fill(
        FillOptions.fillOptions().sortBy(ascending("hour")),
        FillOutputField.value("temperature", "23.6C"),
        FillOutputField.linear("air_pressure")
    )
))
resultsFlow.collect { println(it) }
