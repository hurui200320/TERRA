package info.skyblond.kademlia.node

import info.skyblond.kademlia.utils.hexStringToByteArray
import info.skyblond.kademlia.utils.toHexString
import info.skyblond.kademlia.utils.update
import java.math.BigInteger
import java.util.*
import kotlin.experimental.and
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

    /**
     * Count the leading zero in bit
     * */
    fun countLeadingZero(): Int {
        return this.bytes.takeWhile {
            it.toInt() and 0x80 == 0
        }.sumBy {
            for ((count, i) in (0..7).withIndex()) {
                val currentBit = it.toInt() and (0x80 shr i)
                if (currentBit != 0) {
                    return@sumBy count
                }
            }
            8
        }
    }

    /*
    * TODO:
    *  Custom Serializable? - Have to transmitted by UDP, then it need a way to become bytes
    *  Test case
    * */

    /**
     * Generate new id by a given distance
     * */
    fun generateIdByDistance(distance: Int): KademliaId {
        val result = this.bytes.copyOf()
        // make result[0] the lower bytes
        result.reverse()

        // Change distance by flipping lower bits
        val bytesFlipCount = distance / 8
        val bitsFlipCount = distance % 8
        // flip big chunk
        for (i in 0 until bytesFlipCount) {
            result[i] = result[i] xor (0b11111111.toByte())
        }

        if (bitsFlipCount != 0) {
            // then flip small bits
            val tmp = when(bitsFlipCount){
                1 -> 0b00000001.toByte()
                2 -> 0b00000011.toByte()
                3 -> 0b00000111.toByte()
                4 -> 0b00001111.toByte()
                5 -> 0b00011111.toByte()
                6 -> 0b00111111.toByte()
                7 -> 0b01111111.toByte()
                else -> error("Unexpected bitsFlipCount value: $bitsFlipCount, should in range of 0..7")
            }

            result[bytesFlipCount] = result[bytesFlipCount] xor tmp
        }

        // reverse back
        result.reverse()

        return KademliaId(result)
    }

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