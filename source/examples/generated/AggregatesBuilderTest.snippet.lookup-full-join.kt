val variables = listOf(
    Variable("order_item", "\$item"),
    Variable("order_qty", "\$ordered")
)
val pipeline = listOf(
    Aggregates.match(
        Filters.expr(
            Document("\$and", listOf(
                Document("\$eq", listOf("$\$order_item", "\$stock_item")),
                Document("\$gte", listOf("\$instock", "$\$order_qty"))
            ))
        )
    ),
    Aggregates.project(
        Projections.fields(
            Projections.exclude("customerId", "stock_item"),
            Projections.excludeId()
        )
    )
)
val innerJoinLookup =
    Aggregates.lookup("warehouses", variables, pipeline, "stockdata")
