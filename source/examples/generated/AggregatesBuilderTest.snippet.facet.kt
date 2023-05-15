facet(
    Facet(
        "Screen Sizes",
        bucketAuto(
            "\$screenSize",
            5,
            BucketAutoOptions().output(sum("count", 1))
        )
    ),
    Facet(
        "Manufacturer",
        sortByCount("\$manufacturer"),
        limit(5)
    )
)
