package info.skyblond.kademlia.utils

import com.google.gson.*
import info.skyblond.kademlia.node.KademliaId

/**
 * The global gson object with custom serializer.
 * */
object GsonObject {
    val gson: Gson

    init {
        val gsonBuilder = GsonBuilder()

        // NodeId serializer and deserializer
        gsonBuilder.registerTypeAdapter(KademliaId::class.java, JsonSerializer<KademliaId> { src, _, context ->
            context.serialize(src.hexString)
        })
        gsonBuilder.registerTypeAdapter(KademliaId::class.java, JsonDeserializer { src, _, context ->
            KademliaId().apply {
                this.hexString = context.deserialize(src, String::class.java)
            }
        })

        gsonBuilder.setPrettyPrinting()

        // create gson obj
        gson = gsonBuilder.create()
    }
}