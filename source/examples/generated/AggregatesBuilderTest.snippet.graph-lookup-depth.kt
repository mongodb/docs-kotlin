Aggregates.graphLookup(
    "employees", "\$reportsTo", "reportsTo", "name", "reportingHierarchy",
    GraphLookupOptions().maxDepth(2).depthField("degrees")
)
