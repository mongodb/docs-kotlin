Aggregates.graphLookup(
    "employees",
    "\$${Employee::reportsTo.name}", Employee::reportsTo.name, Employee::name.name, Results::reportingHierarchy.name,
    GraphLookupOptions().maxDepth(2).depthField(Depth::degrees.name)
)
