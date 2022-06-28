package io.deeplay.qlab.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NormalizerKtTest {

    private val eps = 1E-6f
    private val data = listOf(0, -1, 2, 8, 1, 1, 4, -6, 0)

    @Test
    fun meanTest() {
        assertEquals(1.0f, data.mean { it.toFloat() }, eps)
    }

    @Test
    fun standardDeviationTest() {
        assertEquals(3.559026084010437f, data.standardDeviation { it.toFloat() }, eps)
    }

    @Test
    fun quantileTest() {
        assertEquals(2f, data.quantile(0.75f) { it.toFloat() }, eps)
        assertEquals(-1f, data.quantile(0.25f) { it.toFloat() }, eps)
    }

    @Test
    fun quantileMedianTest() {
        assertEquals(data.median { it.toFloat() }, data.quantile(0.5f) { it.toFloat() }, eps)
    }
}
