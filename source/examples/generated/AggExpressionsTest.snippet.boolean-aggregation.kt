val temperature = current().getInteger("temperature")

listOf(project(fields(
    computed("extremeTemp", temperature
        .lt(of(10))
        .or(temperature.gt(of(95))))
)))
