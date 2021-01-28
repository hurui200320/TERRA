package info.skyblond.kademlia.http

import info.skyblond.kademlia.KademliaConfig
import info.skyblond.kademlia.node.Node
import info.skyblond.kademlia.routing.KademliaRoutingTable
import info.skyblond.kademlia.rpc.KademliaRPC
import info.skyblond.kademlia.utils.GsonObject
import io.javalin.Javalin
import io.javalin.http.BadRequestResponse
import io.javalin.http.MethodNotAllowedResponse
import io.javalin.http.ServiceUnavailableResponse
import io.javalin.plugin.json.FromJsonMapper
import io.javalin.plugin.json.JavalinJson
import io.javalin.plugin.json.ToJsonMapper
import org.slf4j.LoggerFactory

/**
 * Kademlia protocol through http apis.
 * A server for receiving incoming message
 * */
class KadNodeHttpServer(
    val localNode: Node,
    val routingTable: KademliaRoutingTable
) {
    private val logger = LoggerFactory.getLogger(KadNodeHttpServer::class.java)
    private val app = Javalin.create()
    private var started = false

    init {
        JavalinJson.fromJsonMapper = object : FromJsonMapper {
            override fun <T> map(json: String, targetClass: Class<T>) = GsonObject.gson.fromJson(json, targetClass)
        }

        JavalinJson.toJsonMapper = object : ToJsonMapper {
            override fun map(obj: Any): String = GsonObject.gson.toJson(obj)
        }

        app.post("/") { ctx ->
            val rpc = ctx.bodyAsClass(KademliaRPC::class.java)
            logger.debug("Incoming rpc: $rpc, validate: $${rpc.validate()}")
            if (!rpc.validate()) {
                throw BadRequestResponse("RPC NOT VALIDATE")
            }
            when (rpc.operation) {
                KademliaRPC.Operation.PING -> throw ServiceUnavailableResponse()
                KademliaRPC.Operation.STORE -> ctx.json(doFindNode(rpc))
                KademliaRPC.Operation.FIND_NODE -> throw ServiceUnavailableResponse()
                KademliaRPC.Operation.FIND_KEY -> throw ServiceUnavailableResponse()
                // PING, STORE and FIND are ok to be request
                // ACK and RESPOND are used to make json respond
                else -> throw MethodNotAllowedResponse("ILLEGAL OPERATION: ${rpc.operation}", mapOf())
            }
        }
        app.get("/routing") { ctx ->
            ctx.json(routingTable)
        }
    }

    private fun doFindNode(rpc: KademliaRPC): KademliaRPC {
        // Find k closet nodes and return
        // first update the origin node
        routingTable.insert(rpc.origin)
        // then pick k closet nodes and return
        return KademliaRPC(
            KademliaRPC.Operation.RESPOND,
            localNode,
            rpc.kadId,
            null,
            routingTable.findKClosestNode(rpc.kadId!!, KademliaConfig.BUCKET_SIZE)
        )
    }

    fun start() {
        if (!started) {
            started = true
            Thread { app.start(localNode.ip.hostName, localNode.port) }.start()
        }
    }

    fun stop() {
        if (started){
            started = false
            app.stop()
        }
    }
}