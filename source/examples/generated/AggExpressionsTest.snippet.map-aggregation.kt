val warehouses = current().getMap<MqlNumber>("warehouses")

listOf(project(fields(
    computed("totalInventory", warehouses
        .entries()
        .sum { v -> v.getValue() })
)))
