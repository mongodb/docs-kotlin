Aggregates.graphLookup(
    "employees", "\$reportsTo", "reportsTo", "name", "reportingHierarchy"
)
