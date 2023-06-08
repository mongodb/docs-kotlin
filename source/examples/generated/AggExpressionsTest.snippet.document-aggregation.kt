val address = current().getDocument("mailing.address")

    listOf(match(expr(address
        .getString("state")
        .eq(of("WA"))
    )))
