package info.skyblond.kademlia.node

import java.math.BigInteger
import java.net.InetAddress
import java.net.InetSocketAddress


/**
 * Node representation in the k-bucket
 * */
data class Node(
    val ip: InetAddress,
    val port: Int,
    val nodeId: KademliaId
) {
    /**
     * Create a SocketAddress for this node
     */
    @Transient
    val socketAddress: InetSocketAddress = InetSocketAddress(this.ip, port)

    companion object {
        /**
         * Get a comparator with third key. This will compare the distances between the third key.
         * Useful when finding the closest key to a specific key(as the third key).
         * Compare with null is not defined.
         *
         * By specify target is all zero, it's compare two key with absolut value.
         * */
        fun getComparator(target: KademliaId): Comparator<Node> = Comparator { o1, o2 ->
            // b1, b2 should be positive number, ensured by toBigInteger()
            val b1: BigInteger = o1.nodeId.xor(target).toBigInteger()
            val b2: BigInteger = o2.nodeId.xor(target).toBigInteger()

            b1.compareTo(b2)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (nodeId != other.nodeId) return false

        return true
    }

    /*
    * TODO:
    *  Custom Serializable? - Have to transmitted by UDP, then it need a way to become bytes
    *  Test case
    * */

    override fun hashCode(): Int {
        return nodeId.hashCode()
    }
}