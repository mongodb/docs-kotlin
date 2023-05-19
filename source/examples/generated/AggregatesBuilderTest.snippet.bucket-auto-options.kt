bucketAuto(
    "\$${Screen::price.name}", 5,
    BucketAutoOptions()
        .granularity(BucketGranularity.POWERSOF2)
        .output(sum(Bucket::count.name, 1), avg(Bucket::avgPrice.name, "\$${Screen::price.name}"))
        )
