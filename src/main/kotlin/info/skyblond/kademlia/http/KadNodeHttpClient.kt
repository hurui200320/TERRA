package info.skyblond.kademlia.http

import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node
import info.skyblond.kademlia.routing.KademliaRoutingTable
import info.skyblond.kademlia.utils.SequentialTreeSet
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

/**
 * Kademlia protocol through http apis.
 * A client for sending outgoing request
 * */
class KadNodeHttpClient(
    val localNode: Node,
    val routingTable: KademliaRoutingTable
) {
    private val ASKED = "asked"
    private val PENDING = "pending"
    private val TIMEOUT = "timeout"

    // send requests to 3 nodes a time
    private val alpha = 3
    private val requestedNodes: HashMap<Node, String> = HashMap()


    /**
     * Do RPC FIND_NODE
     * */
    fun findNode(id: KademliaId) {
        // first set local node as asked
        requestedNodes[localNode] = ASKED
        // add all nodes to a local to do table
        val todoTable = SequentialTreeSet(Node.getComparator(id))
        todoTable.addAll(routingTable.getNodes())
        // then loop, here we request all known nodes, but also can be a timeout
        val executor = Executors.newFixedThreadPool(alpha) as ThreadPoolExecutor
        while (todoTable.isNotEmpty() && executor.activeCount == 0) {
            TODO()
        }
        // done, update all timeout nodes
        requestedNodes.filter { it.value == TIMEOUT }.keys.forEach { routingTable.markStaleRouteByNode(it) }
    }
}