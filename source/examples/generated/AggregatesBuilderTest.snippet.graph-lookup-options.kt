Aggregates.graphLookup(
    "employees", "\$reportsTo", "reportsTo", "name", "reportingHierarchy",
    GraphLookupOptions().maxDepth(1).restrictSearchWithMatch(
        Filters.eq("department", "Engineering")
    )
)
