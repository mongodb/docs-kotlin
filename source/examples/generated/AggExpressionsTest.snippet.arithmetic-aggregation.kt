val month = current().getDate("date").month(of("UTC"))
val precip = current().getInteger("precipitation")

listOf(
    group(month,
        avg("avgPrecipMM", precip.multiply(25.4))
))
