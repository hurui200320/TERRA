package info.skyblond.kademlia.rpc

import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node

data class KademliaRPC(
    /**
     * The RPC Operation you want perform
     * */
    val operation: Operation,
    /**
     * The origin node info(for respond)
     * */
    val origin: Node,
    /**
     *
     * */
    val kadId: KademliaId?,
    val value: ByteArray?,
    val nodes: List<Node>?
) {
    enum class Operation {
        PING, STORE, FIND_NODE, FIND_KEY,
        ACK, RESPOND
    }

    fun validate(): Boolean {
        return when (operation) {
            Operation.PING -> {
                true
            }
            Operation.STORE -> {
                kadId != null && value != null
            }
            Operation.FIND_NODE -> {
                kadId != null
            }
            Operation.FIND_KEY -> {
                kadId != null
            }
            Operation.RESPOND -> {
                value != null || nodes != null
            }
            Operation.ACK -> {
                true
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KademliaRPC

        if (operation != other.operation) return false
        if (kadId != other.kadId) return false
        if (value != null) {
            if (other.value == null) return false
            if (!value.contentEquals(other.value)) return false
        } else if (other.value != null) return false
        if (nodes != other.nodes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operation.hashCode()
        result = 31 * result + (kadId?.hashCode() ?: 0)
        result = 31 * result + (value?.contentHashCode() ?: 0)
        result = 31 * result + (nodes?.hashCode() ?: 0)
        return result
    }
}