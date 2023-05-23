Aggregates.graphLookup(
    "contacts",
    "\$${Users::friends.name}", Users::friends.name, Users::name.name, Results::socialNetwork.name,
    GraphLookupOptions().maxDepth(1).restrictSearchWithMatch(
        Filters.eq(Users::hobbies.name, "golf")
    )
)
