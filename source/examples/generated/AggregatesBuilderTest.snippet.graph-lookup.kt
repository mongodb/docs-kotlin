Aggregates.graphLookup(
    "employees",
    "\$${Employee::reportsTo.name}", Employee::reportsTo.name, Employee::name.name, Results::reportingHierarchy.name
)
