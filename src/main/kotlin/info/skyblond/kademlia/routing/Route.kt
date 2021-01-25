package info.skyblond.kademlia.routing

import info.skyblond.kademlia.node.Node

/**
 * Entry of the routing table
 * */
class Route(
    /**
     * Internal node reference
     * */
    val node: Node
) : Comparable<Route> {
    /**
     * Last seen by timestamp(in the unit of second)
     * */
    var lastSeen: Long = System.currentTimeMillis() / 1000
        private set

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
     * Thus, one with smaller [lastSeen] is considering smaller
     * */
    override fun compareTo(other: Route): Int {
        return if (this.node == other.node)
            0
        else
            this.lastSeen.compareTo(other.lastSeen)
    }

    override fun toString(): String {
        return "Route(node=$node, lastSeen=$lastSeen, staleCount=$staleCount)"
    }

    companion object {
        /**
         * [Comparator] for [java.util.TreeSet]
         * @see compareTo
         * */
        fun getComparator(): Comparator<Route> = Comparator { o1, o2 -> o1.compareTo(o2) }
    }


}