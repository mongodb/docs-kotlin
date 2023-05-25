bucketAuto(
    "\$${Screen::price.name}", 5,
    BucketAutoOptions()
        .granularity(BucketGranularity.POWERSOF2)
        .output(sum(Results::count.name, 1), avg(Results::avgPrice.name, "\$${Screen::price.name}"))
        )
