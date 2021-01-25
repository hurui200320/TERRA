package info.skyblond.kademlia.utils

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HexStringExtensionKtTest {
    @Test
    fun toHexString() {
        assertEquals(
            (0..255).toList()
                .joinToString("") { String.format("%2s", Integer.toHexString(it)).replace(" ", "0").toUpperCase() },
            (0..255).toList().map { it.toByte() }.toByteArray().toHexString()
        )
    }

    @Test
    fun hexStringToByteArray() {
        assertArrayEquals(
            (0..255).toList().map { it.toByte() }.toByteArray(),
            (0..255).toList()
                .joinToString("") { String.format("%2s", Integer.toHexString(it)).replace(" ", "0") }
                .hexStringToByteArray(),
        )
    }
}