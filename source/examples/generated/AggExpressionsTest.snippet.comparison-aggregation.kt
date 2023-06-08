val location = current().getString("location")

listOf(match(expr(location.eq(of("California")))))
