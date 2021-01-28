package info.skyblond.kademlia.routing

import info.skyblond.kademlia.node.Node

/**
 * Entry of the routing table
 * */
class Route(
    /**
     * Internal node reference
     * */
    val node: Node,

    /**
     * Last seen by timestamp(in the unit of second)
     * */
    var lastSeen: Long = System.currentTimeMillis() / 1000
) : Comparable<Route> {
    /**
     * Update [lastSeen] with current timestamp
     * */
    fun refreshLastSeen() {
        synchronized(this.lastSeen) {
            this.lastSeen = System.currentTimeMillis() / 1000
        }
    }

    /**
     * Stale count, defined by the time of failed respond
     * */
    var staleCount = 0
        private set

    /**
     * Increase stale count by one
     * */
    fun increaseStaleCount() {
        synchronized(staleCount) {
            staleCount++
        }
    }

    /**
     * Clear stale count to 0
     * */
    fun resetStaleCount() {
        synchronized(staleCount) {
            staleCount = 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (node != other.node) return false

        return true
    }

    override fun hashCode(): Int {
        return node.hashCode()
    }

    /**
     * Routes are compared as follow(When sorted asc by default):
     * 1. The most recent see one is the biggest(set at the bottom of k-bucket)
     * 2. The least recent see one is the smallest(set on top of k-bucket)
     * 3. Only two route with same node are same(return 0 by this function)
     * Thus, one with smaller [lastSeen] is considering smaller
     * */
    override fun compareTo(other: Route): Int {
        return when {
            this.node == other.node -> 0
            // Note: We can't simply return `this.lastSeen.compareTo(other.lastSeen)`
            // Since that might be same, but not means two routes are same
            this.lastSeen > other.lastSeen -> 1
            else -> -1
        }
    }

    override fun toString(): String {
        return "Route(node=${node.nodeId.hexString}, lastSeen=$lastSeen, staleCount=$staleCount)"
    }

    companion object {
        /**
         * [Comparator] for [java.util.TreeSet]
         * @see compareTo
         * */
        fun getComparator(): Comparator<Route> = Comparator { o1, o2 -> o1.compareTo(o2) }
    }


}