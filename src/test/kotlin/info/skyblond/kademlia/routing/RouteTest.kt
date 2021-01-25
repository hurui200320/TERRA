package info.skyblond.kademlia.routing

import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.net.InetAddress
import kotlin.random.Random
import kotlin.test.assertTrue

internal class RouteTest {

    @Test
    fun refreshLastSeen() {
        val route = Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)))
        val currentLastSeen = route.lastSeen
        Thread.sleep(3000)
        route.refreshLastSeen()
        assertNotEquals(route.lastSeen, currentLastSeen)
    }

    @Test
    fun increaseStaleCount() {
        val route = Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)))
        val currentStaleCount = route.staleCount
        route.increaseStaleCount()
        assertEquals(currentStaleCount + 1, route.staleCount)
    }

    @Test
    fun resetStaleCount() {
        val route = Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)))
        route.increaseStaleCount()
        route.increaseStaleCount()
        route.increaseStaleCount()
        assertNotEquals(route.staleCount, 0)
        route.resetStaleCount()
        assertEquals(0, route.staleCount)
    }

    @Test
    fun testCompare() {
        val list = mutableListOf(
            Route(Node(InetAddress.getLocalHost(), 80, KademliaId(Random)))
        )

        for (i in 1..3) {
            Thread.sleep(1500)
            list.add(Route(Node(InetAddress.getLocalHost(), 81, KademliaId(Random))))
        }

        list.shuffle()
        list.sort()

        assertTrue("The first node is list is not the least seen one") {
            list.first().lastSeen == list.minOf { it.lastSeen }
                    && list.last().lastSeen == list.maxOf { it.lastSeen }
        }
    }
}