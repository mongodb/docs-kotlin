Aggregates.search(
    SearchOperator.text(
        SearchPath.fieldPath("title"), "Future"
    ),
    SearchOptions.searchOptions().index("title")
)
