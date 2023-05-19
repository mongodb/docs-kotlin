bucket("\$${Screen::screenSize.name}", listOf(0, 24, 32, 50, 70),
    BucketOptions()
        .defaultBucket("monster")
        .output(
            sum(Results::count.name, 1),
            push(Results::matches.name, "\$${Screen::screenSize.name}")
        )
)
