val deliveryDate = current().getString("deliveryDate")

listOf(match(expr(deliveryDate
    .parseDate()
    .dayOfWeek(of("America/New_York"))
    .eq(of(2))
)))
