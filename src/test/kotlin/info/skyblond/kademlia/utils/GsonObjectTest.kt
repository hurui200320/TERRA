package info.skyblond.kademlia.utils

import info.skyblond.kademlia.node.KademliaId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class GsonObjectTest {
    private val seed = System.currentTimeMillis()
    init {
        println("Random seed: $seed")
    }

    @Test
    fun testNodeIdSerialization(){
        val random = Random(seed)
        for (i in 1..5000) {
            val nodeId = KademliaId(random)
            val result = GsonObject.gson.toJson(nodeId)
            assertEquals("\"${nodeId.hexString}\"", result)
        }
    }

    @Test
    fun testNodeIdDeserialization1(){
        val random = Random(seed)
        for (i in 1..5000) {
            val nodeId = KademliaId(random)
            val result = GsonObject.gson.fromJson("\"${nodeId.hexString}\"", KademliaId::class.java)
            assertEquals(nodeId.hexString, result.hexString)
        }
    }

    @Test
    fun testNodeIdDeserialization2(){
        assertThrows(IllegalArgumentException::class.java) {
            GsonObject.gson.fromJson("\"asd123\"", KademliaId::class.java)
        }
        assertThrows(IllegalArgumentException::class.java) {
            GsonObject.gson.fromJson("\"ZZZZZZZZzzzzzzzzZZZZZZZZzzzzzzzzZZZZZZZZ\"", KademliaId::class.java)
        }
    }
}