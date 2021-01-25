import info.skyblond.kademlia.node.KademliaId
import info.skyblond.kademlia.node.Node
import info.skyblond.kademlia.utils.GsonObject
import org.slf4j.LoggerFactory
import java.net.InetAddress
import kotlin.random.Random


fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Application")

    val node = Node(InetAddress.getByName("192.168.1.80"), 80, KademliaId(Random))
    println(node)
    println(GsonObject.gson.toJson(node))

    logger.info("Done!")
}