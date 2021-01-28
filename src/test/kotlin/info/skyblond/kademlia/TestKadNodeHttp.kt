package info.skyblond.kademlia

import info.skyblond.kademlia.http.KadNodeHttpClient
import info.skyblond.kademlia.http.KadNodeHttpServer
import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node
import info.skyblond.kademlia.routing.KademliaRoutingTable
import org.junit.jupiter.api.Test
import java.net.InetAddress
import kotlin.random.Random

class TestKadNodeHttp {
    @Test
    fun main() {
        val localNode1 = Node(InetAddress.getByName("127.0.0.1"), 8080, KademliaId(Random))
        val routingTable1 = KademliaRoutingTable(localNode1)

        val server1 = KadNodeHttpServer(localNode1, routingTable1)
        val client1 = KadNodeHttpClient(localNode1, routingTable1)
        server1.start()

        val localNode2 = Node(InetAddress.getByName("127.0.0.1"), 8081, KademliaId(Random))
        val routingTable2 = KademliaRoutingTable(localNode2)

        val server2 = KadNodeHttpServer(localNode2, routingTable2)
        val client2 = KadNodeHttpClient(localNode2, routingTable2)
        server2.start()

        routingTable1.insert(localNode2)
        client1.findNode(localNode2.nodeId)

        while (true){}
    }
}