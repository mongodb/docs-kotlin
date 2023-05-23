Aggregates.graphLookup(
    "contacts",
    "\$${Users::friends.name}", Users::friends.name, Users::name.name, Results::socialNetwork.name
)
