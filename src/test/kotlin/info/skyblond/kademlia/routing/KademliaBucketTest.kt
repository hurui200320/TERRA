package info.skyblond.kademlia.routing

import info.skyblond.kademlia.KademliaConfig
import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.net.InetAddress
import kotlin.random.Random

internal class KademliaBucketTest {

    @Test
    fun mainTest() {
        require(KademliaConfig.BUCKET_SIZE > 3) { "To finish this test, BUCKET_SIZE must bigger than 3" }
        require(KademliaConfig.REPLACEMENT_CACHE_SIZE > 3) { "To finish this test, REPLACEMENT_CACHE_SIZE must bigger than 3" }
        val bucket = KademliaBucket(0)
        for (i in 1..KademliaConfig.BUCKET_SIZE) {
            bucket.insert(Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)), i.toLong()))
        }
        // should all insert into routes set, replacement cache is empty
        assertEquals(KademliaConfig.BUCKET_SIZE, bucket.getRoutes().size)
        assertEquals(0, bucket.getReplacementCache().size)

        // Then should insert into replacement cache
        for (i in 1..KademliaConfig.REPLACEMENT_CACHE_SIZE) {
            bucket.insert(Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)), KademliaConfig.BUCKET_SIZE + i.toLong()))
        }
        assertEquals(KademliaConfig.BUCKET_SIZE, bucket.getRoutes().size)
        assertEquals(KademliaConfig.REPLACEMENT_CACHE_SIZE, bucket.getReplacementCache().size)

        // keep the least recent seen one
        val shouldBeReplaced = bucket.getReplacementCache().minByOrNull { it.lastSeen }!!
        // insert into replacement cache, should replace that one
        bucket.insert(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)))
        assertFalse(bucket.getReplacementCache().contains(shouldBeReplaced))

        // stale 3 nodes
        bucket.getRoutes().shuffled().take(3).forEach {
            it.increaseStaleCount()
        }

        // then insert, should drop the new one and delete the stales
        // thus the replacement cache should left with SIZE-3
        println("Nodes for replacement: " + bucket.getReplacementCache().map { it.node.nodeId })
        println("Nodes in bucket: " + bucket.getRoutes().map { it.node.nodeId })
        val inserted = Node(InetAddress.getLocalHost(), 80, KademliaId(Random))
        println("Inserting node: " + inserted.nodeId)
        bucket.insert(inserted)
        assertEquals(KademliaConfig.BUCKET_SIZE, bucket.getRoutes().size)
        assertEquals(KademliaConfig.REPLACEMENT_CACHE_SIZE - 3, bucket.getReplacementCache().size)
        println("Nodes in bucket: " + bucket.getRoutes().map { it.node.nodeId })
        // TODO test What if stale 3 but only 2 available for replacement
        // TODO more detailed test case
    }
}