Aggregates.group("\$${Order::customerId.name}",
    sum(Results::totalQuantity.name, "\$${Order::ordered.name}"),
    avg(Results::averageQuantity.name, "\$${Order::ordered.name}")
)
