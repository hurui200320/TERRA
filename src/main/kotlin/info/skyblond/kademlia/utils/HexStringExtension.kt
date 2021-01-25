package info.skyblond.kademlia.utils

private const val HEX_CHARS = "0123456789ABCDEF"

/**
 * Converting a [ByteArray] to a hex [String].
 * Reverse function of [String.hexStringToByteArray]
 * @see String.hexStringToByteArray
 * */
fun ByteArray.toHexString(): String {
    val result = StringBuffer()
    forEach {
        val b = it.toInt()
        val higher = (b and 0xF0).ushr(4)
        val lower = b and 0x0F
        result.append(HEX_CHARS[higher])
        result.append(HEX_CHARS[lower])
    }
    return result.toString()
}

/**
 * Update the content of a [ByteArray] by given a target [ByteArray], without changing the object reference.
 * */
fun ByteArray.update(target: ByteArray) {
    require(this.size <= target.size) { "Cannot update with given array. Current length ${this.size}, target array length: ${target.size}" }
    for (i in this.indices) {
        this[i] = target[i]
    }
}

/**
 * Convert a hex [String] back into a [ByteArray].
 * Reverse function of [ByteArray.toHexString]
 * @see ByteArray.toHexString
 * */
fun String.hexStringToByteArray(): ByteArray {
    val result = ByteArray(length / 2)
    for (i in 0 until length step 2) {
        val higher = HEX_CHARS.indexOf(this[i].toUpperCase())
        require(higher != -1) { "Invalid hex char found: ${this[i]}" }
        val lower = HEX_CHARS.indexOf(this[i + 1].toUpperCase())
        require(lower != -1) { "Invalid hex char found: ${this[i + 1]}" }
        val b = higher.shl(4).or(lower)
        result[i.shr(1)] = b.toByte()
    }

    return result
}

/**
 * Query a bit. `true` -> 1; `false` -> 0
 * */
fun ByteArray.getBit(index: Int): Boolean {
    val b = this[index / java.lang.Byte.SIZE].toInt()
    val i = 0x80 shr index % java.lang.Byte.SIZE
    return (b and i) != 0
}

/**
 * Set bit.
 * */
fun ByteArray.setBit(index: Int, value: Boolean) {
    val b = this[index / java.lang.Byte.SIZE].toInt()
    // mask, 00010000
    val i = 0x80 shr index % java.lang.Byte.SIZE
    if (value) {
        // writing 1 -> XXXXXXXX | 00010000 -> XXX1XXXX
        this[index / java.lang.Byte.SIZE] = b.or(i).toByte()
    } else {
        // writing 0 -> XXXXXXXX & 11101111 -> XXX0XXXX
        this[index / java.lang.Byte.SIZE] = b.and(i.inv()).toByte()
    }
}