package info.skyblond.kademlia.node

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