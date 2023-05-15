bucket("\$screenSize", listOf(0, 24, 32, 50, 70),
    BucketOptions()
        .defaultBucket(200)
        .output(
            sum("count", 1),
            push("matches", "\$screenSize")
        )
)
