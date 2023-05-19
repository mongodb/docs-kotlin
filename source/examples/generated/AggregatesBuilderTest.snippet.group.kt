Aggregates.group("\$customerId",
    sum(Results::totalQuantity.name, "\$ordered"),
    avg(Results::averageQuantity.name, "\$ordered"))
