package info.skyblond.kademlia.routing

import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node
import java.util.*


class KademliaRoutingTable(
    /**
     * Current local node.
     * */
    val localNode: Node
) {

    /**
     * All level of k-bucket in the tree
     * */
    private val buckets = Array(KademliaId.ID_LENGTH + 1) { i -> KademliaBucket(i) }

    init {
        this.insert(localNode)
    }

    /**
     * Insert a route into routing table
     * */
    @Synchronized
    fun insert(route: Route) {
        this.buckets[this.getBucketId(route.node.nodeId)].insert(route)
    }

    /**
     * Insert a node into routing table
     * */
    @Synchronized
    fun insert(node: Node) {
        this.buckets[this.getBucketId(node.nodeId)].insert(node)
    }

    /**
     * Calculate bucket index of given id.
     * i.e.: The leading zero of xor distance.
     * =0 means share no same prefix, thus stored in bucket[0](top of the tree)
     * =160 same as local node id, thus stored in bucket[160](bottom of the tree)
     * */
    @Synchronized
    private fun getBucketId(id: KademliaId): Int {
        return this.localNode.nodeId.xor(id).countLeadingZero()
    }

    /**
     * Return k nodes with closest distance towards a given id
     * */
    @Synchronized
    fun findKClosestNode(id: KademliaId, k: Int): List<Node> {
        // using TreeSet to select the top k closest nodes
        val sortedSet: TreeSet<Node> = TreeSet<Node>(Node.getComparator(id))
        sortedSet.addAll(this.getNodes())
        // then take k nodes with minimum distance towards `id`
        return sortedSet.take(k)
    }

    /**
     * Dump all nodes
     * */
    @Synchronized
    fun getNodes(): List<Node> {
        return this.buckets.flatMap { bucket -> bucket.getRoutes().map { it.node } }
    }

    /**
     * Dump all routes
     * */
    @Synchronized
    fun getRoutes(): List<Route> {
        return this.buckets.flatMap { bucket -> bucket.getRoutes() }
    }

    /**
     * Dump all buckets
     * */
    @Synchronized
    fun getBuckets(): Array<KademliaBucket> {
        return this.buckets.copyOf()
    }

    /**
     * Load buckets
     * */
    @Synchronized
    fun loadBuckets(source: Array<KademliaBucket>) {
        for (i in this.buckets.indices) {
            this.buckets[i] = source[i]
        }
    }

    /**
     * Call this function if a node failed to respond
     * */
    @Synchronized
    fun markStaleRouteByNode(node: Node) {
        this.buckets[this.getBucketId(node.nodeId)].removeNode(node)
    }

    /**
     * Call this function if some routes failed to respond
     * */
    @Synchronized
    fun markStaleRoutes(routes: Collection<Route>) {
        routes.forEach {
            this.markStaleRouteByNode(it.node)
        }
    }

    override fun toString(): String {
        return "KademliaRoutingTable(localNode=$localNode, buckets=${buckets.contentToString()})"
    }

}