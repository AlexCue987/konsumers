package com.tgt.trans.common.aggregator2.consumers

import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ConsumerTest {
    @Test
    fun worksWithSequence() {
        val actual = (1..3).asSequence().consume(max2())
        assertEquals(listOf(Optional.of(3)), actual)
    }

    @Test
    fun worksWithIterable() {
        val actual = (1..3).asIterable().consume(max2())
        assertEquals(listOf(Optional.of(3)), actual)
    }

    @Test
    fun mapTo() {
        val sut = com.tgt.trans.common.aggregator2.decorators.mapTo { a: Int -> a + 1 }.count()
        sut.process(1)
        sut.process(2)
        assertEquals(2L, sut.results())
    }

    @Test
    fun ratio() {
        val sut = com.tgt.trans.common.aggregator2.decorators.mapTo { a: Int -> a + 1 }.ratioOf { it%2 == 1 }
        (1..5).forEach { sut.process(it) }
        assertEquals(Ratio2(2, 5), sut.results())
    }

    @Test
    fun filterOn_standalone() {
        val actual = listOf(1, 2, 3, 4, 5).consume(filterOn { it: Int -> it % 2 == 0 }.count())
        assertEquals(2L, actual[0])
    }

    @Test
    fun filterOn_chainedAfter() {
        val actual = listOf(1, 2, 3, 4, 5).consume(
                com.tgt.trans.common.aggregator2.decorators.mapTo { a: Int -> a + 1 }.filterOn { it: Int -> it % 2 == 0 }.count(),
                counter())
        assertEquals(listOf(3L, 5L), actual)
    }

    @Test
    fun filterOnMapTo() {
        val actual = listOf(1, 2, 3, 4, 5).consume(
                filterOn { it: Int -> it % 2 == 0 }.mapTo{ a: Int -> a + 1}.count(),
                counter())
        assertEquals(listOf(2L, 5L), actual)
    }
}
