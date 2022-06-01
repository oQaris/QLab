package io.deeplay.qlab.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NormalizerKtTest {

    private val data = listOf(0, -1, 2, 8, 1, 1, 4, -6, 0)

    @Test
    fun meanTest() {
        assertEquals(1.0, data.mean { it.toFloat() })
    }

    @Test
    fun standardDeviationTest() {
        assertEquals(3.559026084010437, data.standardDeviation { it.toFloat() })
    }
}
