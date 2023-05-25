class MonolightCodecProvider : CodecProvider {
    override fun <T> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        return if (clazz == Monolight::class.java) {
            MonolightCodec(registry) as Codec<T>
        } else null // return null when not a provider for the requested class
    }
}
