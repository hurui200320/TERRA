package info.skyblond.kademlia

@Suppress("MemberVisibilityCanBePrivate")
object KademliaConfig {
    // TODO move to Kad-Bucket class
    /**
     * The k-bucket size, typical: 20.
     * For test, set to 7
     * */
    const val BUCKET_SIZE = 7

    /**
     * The replacement cache size
     * */
    const val REPLACEMENT_CACHE_SIZE = 4

    init {
        require(BUCKET_SIZE > 0) {"BUCKET_SIZE must bigger than 0"}
        require(REPLACEMENT_CACHE_SIZE > 0) {"REPLACEMENT_CACHE_SIZE must bigger than 0"}
    }
}