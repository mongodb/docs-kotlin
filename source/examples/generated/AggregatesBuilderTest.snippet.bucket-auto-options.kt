bucketAuto(
    "\$price", 5,
    BucketAutoOptions()
        .granularity(BucketGranularity.POWERSOF2)
        .output(sum("count", 1), avg("avgPrice", "\$price")
        )
)
