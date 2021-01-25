package info.skyblond.kademlia.node

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import info.skyblond.kademlia.utils.hexStringToByteArray
import info.skyblond.kademlia.utils.toHexString
import info.skyblond.kademlia.utils.update
import java.math.BigInteger
import kotlin.experimental.xor
import kotlin.random.Random

/**
 * 160bits NodeId/Key representation.
 * */
class KademliaId {
    /**
     * Internal representation
     * */
    private val bytes = ByteArray(ID_LENGTH / Byte.SIZE_BITS)

    companion object {
        /**
         * Kademlia ID length in bits.
         * */
        const val ID_LENGTH = 160

        init {
            require(ID_LENGTH % Byte.SIZE_BITS == 0) { "ID_LENGTH must be a multiple of ${Byte.SIZE_BITS}" }
        }

        /**
         * Get a comparator with third key. This will compare the distances between the third key.
         * Useful when finding the closest key to a specific key(as the third key).
         * Compare with null is not defined.
         *
         * By specify target is all zero, it's compare two key with absolut value.
         * */
        fun getComparator(target: KademliaId): Comparator<KademliaId> = Comparator { o1, o2 ->
            // b1, b2 should be positive number, ensured by toBigInteger()
            val b1: BigInteger = o1.xor(target).toBigInteger()
            val b2: BigInteger = o2.xor(target).toBigInteger()

            b1.compareTo(b2)
        }
    }

    /**
     * Create a all zero id by default.
     * @param random [Random] Object to randomize the content, null to keep all zeros
     * */
    constructor(random: Random? = null) {
        if (random != null)
            this.bytes.update(random.nextBytes(this.bytes.size))
    }

    /**
     * Create by hex string
     * */
    constructor(hexString: String) : this() {
        this.hexString = hexString
    }

    /**
     * Create by byte array
     * */
    constructor(bytes: ByteArray) : this() {
        this.bytes.update(bytes)
    }

    /**
     * Convert 160bits NodeId from/to hex string
     * */
    var hexString: String
        get() = bytes.toHexString()
        set(value) {
            require(value.length == bytes.size * 2) { "NodeId has to be ${bytes.size * Byte.SIZE_BITS}bits, i.e. ${bytes.size * 2} hex chars" }
            bytes.update(value.hexStringToByteArray())
        }

    /**
     * Duplicate current node id instance.
     * */
    fun copy(): KademliaId = KademliaId(bytes)

    /**
     * Get a copy of internal bytes
     * */
    fun dump(): ByteArray = this.bytes.copyOf()

    /**
     * Do XOR operation
     * */
    fun xor(other: KademliaId): KademliaId {
        val resultByteArray = ByteArray(this.bytes.size)
        for (i in this.bytes.indices) {
            resultByteArray[i] = this.bytes[i] xor other.bytes[i]
        }
        return KademliaId(resultByteArray)
    }

    /**
     * For a xor result, this can translate bytes into positive numbers
     * */
    fun toBigInteger(): BigInteger = BigInteger(1, this.bytes)

    /*
    * TODO:
    *  Custom Serializable? - Have to transmitted by UDP, then it need a way to become bytes
    *  generateNodeIdByDistance - Generate ID by some given distance to this ID, xor to flip bits
    *  getDistance - Count the distance in the Tree by level(or height), i.e. id length - count of leading zeros of the result of xor
    *   | this should be implemented in [Node] class.
    *  Test case
    * */

    /**
     * Only the internal bytes are compared if same.
     * */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KademliaId

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String {
        return "NodeId(hexString='$hexString')"
    }
}