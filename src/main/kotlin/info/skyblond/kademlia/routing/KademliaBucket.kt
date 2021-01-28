package info.skyblond.kademlia.routing

import info.skyblond.kademlia.KademliaConfig
import info.skyblond.kademlia.node.Node
import info.skyblond.kademlia.utils.SequentialTreeSet
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.NoSuchElementException


/**
 * The k-bucket in the routing table/tree
 * */
class KademliaBucket(
    /**
     * Bucket's depth in the routing tree
     * */
    val depth: Int
) {
    /**
     * Routes stored in this routing table, least recent seen route set on top
     * */
    private val routes: SequentialTreeSet<Route> = SequentialTreeSet()

    /**
     * store new seen routes when k-bucket is full, least recent seen route set on top
     * */
    private val replacementCache: SequentialTreeSet<Route> = SequentialTreeSet()

    /**
     * Insert a new route into the set.
     * It won't force check the range of ID. This should be ensured by routing table.
     * */
    @Synchronized
    fun insert(route: Route) {
        if (this.routes.contains(route)) {
            // update if already exists
            // remove, update, insert, thus the tree set is updated
            val tmp = this.removeFromRoutes(route)
            tmp.refreshLastSeen()
            tmp.resetStaleCount()
            this.routes.add(tmp)
        } else {
            if (this.routes.size >= KademliaConfig.BUCKET_SIZE) {
                // if k-bucket is full, insert into replacement cache
                // this might drop the node since replacement cache is full
                this.insertIntoReplacementCache(route)
                // check if any stale nodes can be replaced
                // first take the node should be replaced
                val deleted = this.routes
                    .filter { it.staleCount > 0 }
                    .sortedByDescending { it.staleCount }
                    .take(this.replacementCache.size)
                // remove those from routes
                this.routes.removeAll(deleted)
                // then take the route tobe insert
                // least seen on first, reverse to move most seen to first
                val added = this.replacementCache.reversed().take(deleted.size)
                // add into routes
                this.routes.addAll(added)
                // finally remove them from replacement cache
                added.forEach { this.removeFromReplacementCache(it.node) }
            } else {
                // k-bucket not full, just insert
                this.routes.add(route)
            }
        }
    }

    /**
     * Insert a new node into the set.
     * */
    @Synchronized
    fun insert(node: Node) {
        insert(Route(node))
    }

    /**
     * return true if a route has already in the routes set
     * */
    @Synchronized
    fun containsRoute(route: Route): Boolean {
        return this.routes.contains(route)
    }

    /**
     * return true if a node has already in the routes set
     * */
    @Synchronized
    fun containsNode(node: Node): Boolean {
        return containsRoute(Route(node))
    }

    /**
     * Remove route from routes set. Replaced if [replacementCache] is not empty,
     * otherwise just update [Route.staleCount].
     * @return true if a route is removed.
     * */
    @Synchronized
    fun removeRoute(route: Route): Boolean {
        if (!this.routes.contains(route)) {
            // not exists
            return false
        }
        if (replacementCache.isEmpty()) {
            // replacement cache is empty, update stale count
            // must find the one **in the set** with the same id
            this.routes.find { it.node == route.node }!!.increaseStaleCount()
        } else {
            // pick one from replacement cache to replace it
            this.routes.remove(route)
            val tmp = replacementCache.last()
            this.routes.add(tmp)
            replacementCache.remove(tmp)
        }

        return true
    }

    /**
     * Remove a route from routes set.
     * Identical to `this.routes.remove`, but return the obj deleted.
     * @return the route has been deleted
     * */
    @Synchronized
    private fun removeFromRoutes(route: Route): Route {
        for (r in this.routes)
            if (r.node == route.node) {
                this.routes.remove(r)
                return r
            }
        throw NoSuchElementException("The route does not exist in the routes set.")
    }

    /**
     * Remove a node from routes set
     * */
    @Synchronized
    fun removeNode(node: Node): Boolean {
        return removeRoute(Route(node))
    }

    /**
     * Dump routes set into a list
     * */
    @Synchronized
    fun getRoutes(): List<Route> = this.routes.toList()


    /**
     * Dump replacement cache into a list
     * */
    @Synchronized
    fun getReplacementCache(): List<Route> = this.replacementCache.toList()

    /**
     * Insert a route into replacement cache
     * */
    @Synchronized
    private fun insertIntoReplacementCache(route: Route) {
        when {
            this.replacementCache.contains(route) -> {
                // if already in replacement cache, update
                // remove, update, insert to update the position in tree set
                val tmp = this.removeFromReplacementCache(route.node)
                tmp.refreshLastSeen()
                this.replacementCache.add(tmp)
            }
            this.replacementCache.size >= KademliaConfig.REPLACEMENT_CACHE_SIZE -> {
                // replacement cache is full, then replace the first one(the least recent seen one)
                this.replacementCache.remove(this.replacementCache.first())
                // add this one
                this.replacementCache.add(route)
            }
            else -> {
                this.replacementCache.add(route)
            }
        }
    }

    /**
     * Remove a node from replacement cache
     * @return the node deleted
     * */
    @Synchronized
    private fun removeFromReplacementCache(node: Node): Route {
        for (c in replacementCache) {
            if (c.node == node) {
                replacementCache.remove(c)
                return c
            }
        }
        throw NoSuchElementException("Node does not exist in the replacement cache.")
    }
}