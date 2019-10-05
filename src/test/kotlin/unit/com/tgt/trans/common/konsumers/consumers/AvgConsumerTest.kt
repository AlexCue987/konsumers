package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.filterOn
import com.tgt.trans.common.konsumers.transformations.mapTo
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AvgConsumerTest {
    @Test
    fun handlesEmpty() {
        val items = listOf<Int>()
        val sut = avgOfInt()
        val actual = items.consume(sut)
        assertAll(
                { assertTrue(sut.isEmpty()) },
                { assertEquals (Optional.empty<BigDecimal>(), actual[0]) }
        )
    }

    @Test
    fun handlesOneItem() {
        val items = listOf(42)
        val sut = avgOfInt()
        val actual = items.consume(sut)
        assertAll(
                { assertFalse(sut.isEmpty()) },
                { assertEquals (Optional.of(BigDecimal("42.00")), actual[0]) }
        )
    }

    @Test
    fun handlesSeveralItems() {
        val items = listOf(42, 43, 44, 45)
        val sut = avgOfInt()
        val actual = items.consume(sut)
        assertAll(
                { assertFalse(sut.isEmpty()) },
                { assertEquals (Optional.of(BigDecimal("43.50")), actual[0]) }
        )
    }

    @Test
    fun `avgOfLong Works`() {
        val actual = listOf(42L, 43L, 44L, 45L).consume(
            avgOfLong(2),
            filterOn<Long> { it > 43L }.avgOfLong()
        )
        assertEquals(listOf(Optional.of(BigDecimal("43.50")),
            Optional.of(BigDecimal("44.50"))),
            actual)
    }

    @Test
    fun `avgOfBigDecimal Works`() {
        val actual = listOf(BigDecimal.ONE, BigDecimal.TEN).consume(
            avgOfBigDecimal(2),
            filterOn<BigDecimal> { it > BigDecimal("2") }.avgOfBigDecimal()
        )
        assertEquals(listOf(Optional.of(BigDecimal("5.50")),
            Optional.of(BigDecimal("10.00"))),
            actual)
    }

    @Test
    fun severalChainedAveragesWork() {
        val items = listOf(42, 43, 44, 45)
        val actual = items.consume(mapTo<Int, Int> { it / 2 }.avgOfInt(),
            mapTo { a: Int -> a.toLong() }.avgOfLong())
        assertAll(
            { assertEquals (Optional.of(BigDecimal("21.50")), actual[0]) },
            { assertEquals (Optional.of(BigDecimal("43.50")), actual[1]) }
        )
    }
}
