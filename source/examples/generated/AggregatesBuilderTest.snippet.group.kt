Aggregates.group("\$customerId",
    sum("totalQuantity", "\$quantity"),
    avg("averageQuantity", "\$quantity"))
