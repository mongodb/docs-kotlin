Aggregates.graphLookup(
    "employees",
    "\$${Employee::reportsTo.name}", Employee::reportsTo.name, Employee::name.name, Results::reportingHierarchy.name,
    GraphLookupOptions().maxDepth(1).restrictSearchWithMatch(
        Filters.eq(Employee::department.name, "Engineering")
    )
)
