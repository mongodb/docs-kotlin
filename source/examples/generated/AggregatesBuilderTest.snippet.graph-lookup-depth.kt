Aggregates.graphLookup(
    "contacts",
    "\$${Users::friends.name}", Users::friends.name, Users::name.name, Results::socialNetwork.name,
    GraphLookupOptions().maxDepth(2).depthField(Depth::degrees.name)
)
