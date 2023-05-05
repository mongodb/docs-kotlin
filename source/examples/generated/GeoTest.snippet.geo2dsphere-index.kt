collection.createIndex((Indexes.geo2dsphere(
    "${Theater::location.name}.${Theater.Location::geo.name}"))
)
