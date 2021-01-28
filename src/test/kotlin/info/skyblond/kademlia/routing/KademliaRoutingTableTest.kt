package info.skyblond.kademlia.routing

import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class KademliaRoutingTableTest {
    @Test
    fun insertTest() {
        val localKadId = KademliaId(Random)
        println("Local Kad Id: $localKadId")
        val routingTable = KademliaRoutingTable(Node(InetAddress.getLocalHost(), 80, localKadId))
        println("Initial routing table")
        routingTable.printContent()

        for (i in 100..200) {
            routingTable.insert(Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)), i.toLong()))
        }
        routingTable.printContent()
    }

    @Test
    fun genIdTest() {
        val localKadId = KademliaId(Random)
        println("Local Kad Id: $localKadId")
        val routingTable = KademliaRoutingTable(Node(InetAddress.getLocalHost(), 80, localKadId))
        println("Initial routing table")
        routingTable.printContent()

        for (i in 0..KademliaId.ID_LENGTH) {
            val genId = localKadId.generateIdByDistance(i)
            println("i: $i, id: ${genId.hexString}")
            routingTable.insert(Route(Node(InetAddress.getLocalHost(), 80, genId), i.toLong()))
        }
        routingTable.printContent()
    }

    @Test
    fun removeTest() {
        val localKadId = KademliaId(Random)
        println("Local Kad Id: $localKadId")
        val routingTable = KademliaRoutingTable(Node(InetAddress.getLocalHost(), 80, localKadId))
        for (i in 1000..2000) {
            routingTable.insert(Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)), i.toLong()))
        }

        val replaceBucket = routingTable.getBuckets().maxByOrNull { it.getReplacementCache().size }!!
        println("Before stale:")
        replaceBucket.printContent()
        replaceBucket.getRoutes().take(replaceBucket.getReplacementCache().size).forEach {
            println("Stale node: ${it.node.nodeId.hexString}")
            routingTable.markStaleRouteByNode(it.node)
        }
        println("After stale:")
        replaceBucket.printContent()
        assertTrue(replaceBucket.getReplacementCache().isEmpty())
        val oldSize = replaceBucket.getRoutes().size
        replaceBucket.getRoutes().take(replaceBucket.getRoutes().size / 2 + 1).forEach {
            println("Stale node: ${it.node.nodeId.hexString}")
            routingTable.markStaleRouteByNode(it.node)
        }
        println("Stale when replacement cache is empty:")
        replaceBucket.printContent()
        assertEquals(oldSize, replaceBucket.getRoutes().size)
        assertEquals(oldSize / 2 + 1, replaceBucket.getRoutes().filter { it.staleCount > 0 }.size)
    }


    private fun KademliaBucket.printContent() {
        println("-".repeat(50))
        println("\tBucket depth: " + this.depth)
        println("\tBucket content: ")
        for (route in this.getRoutes()) {
            println("\t\t${route.node.nodeId.hexString} last seen:${route.lastSeen} stale:${route.staleCount}")
        }
        if (this.getReplacementCache().isNotEmpty()) {
            println("\tBucket replacement cache: ")
            for (route in this.getReplacementCache()) {
                println("\t\t${route.node.nodeId} last seen:${route.lastSeen} stale:${route.staleCount}")
            }
        }

        println("-".repeat(50))
    }

    private fun KademliaRoutingTable.printContent() {
        println("-".repeat(50))
        println("Local Kad Id: " + this.localNode.nodeId)
        for (bucket in this.getBuckets()) {
            if (bucket.getRoutes().isNotEmpty()) {
                println("\tBucket depth: " + bucket.depth)
                println("\tBucket content: ")
                for (route in bucket.getRoutes()) {
                    println("\t\t${if (route.node == localNode) "(LOCAL) " else ""}${route.node.nodeId.hexString} last seen:${route.lastSeen} stale:${route.staleCount}")
                }
                if (bucket.getReplacementCache().isNotEmpty()) {
                    println("\tBucket replacement cache: ")
                    for (route in bucket.getReplacementCache()) {
                        println("\t\t${route.node.nodeId} last seen:${route.lastSeen} stale:${route.staleCount}")
                    }
                }
            } else {
                // if routes is empty, then replacement should also be empty
                assertTrue(bucket.getReplacementCache().isEmpty())
            }
        }
        println("-".repeat(50))
    }
}