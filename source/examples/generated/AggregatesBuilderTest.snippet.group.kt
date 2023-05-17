Aggregates.group("\$customerId",
    sum("totalQuantity", "\$ordered"),
    avg("averageQuantity", "\$ordered"))
