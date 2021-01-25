package info.skyblond.kademlia

@Suppress("MemberVisibilityCanBePrivate")
object KademliaConfig {
    /**
     * The k-bucket size, typical: 20.
     * For test, set to 7
     * */
    const val BUCKET_SIZE = 7

    /**
     * If [info.skyblond.kademlia.routing.Route.staleCount] bigger than this tolerance,
     * then remove it if any new route are available.
     * Set 0 to remove any route after first time of failed respond.
     * */
    const val STALE_TOLERANCE = 1

    /**
     * The replacement cache size
     * */
    const val REPLACEMENT_CACHE_SIZE = 4

    init {
        require(BUCKET_SIZE > 0) {"BUCKET_SIZE must bigger than 0"}
        require(STALE_TOLERANCE >= 0) {"STALE_TOLERANCE must bigger or equal than 0"}
        require(REPLACEMENT_CACHE_SIZE > 0) {"REPLACEMENT_CACHE_SIZE must bigger than 0"}
    }
}