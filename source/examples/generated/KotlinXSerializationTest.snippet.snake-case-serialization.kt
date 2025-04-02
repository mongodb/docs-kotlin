val myCustomCodec = KotlinSerializerCodec.create<PaintOrder>(
    bsonConfiguration = BsonConfiguration(bsonNamingStrategy = BsonNamingStrategy.SNAKE_CASE)
)
