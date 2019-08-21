package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.counter
import com.tgt.trans.common.aggregator2.consumers.max2
import com.tgt.trans.common.aggregator2.consumers.min2
import com.tgt.trans.common.aggregator2.decorators.allOf2
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ListOfConsumersTest {
    @Test
    fun computesSeveral() {
        val actual = listOf(1, 2, 3, 4, 3).consume(
            allOf2(min2(), max2(), counter())
        )
        assertEquals(listOf(Optional.of(1), Optional.of(4), 5L), actual[0])
    }

    @Test
    fun filtersAndComputesSeveral() {
        val actual = listOf(1, 2, 3, 4, 3).consume(
                filterOn<Int> { it < 4 }.allOf2(min2(), max2(), counter()),
                filterOn<Int> { it > 2 }.allOf2(min2(), max2(), counter()),
                mapTo<Int, Int> { it * 2 }.filterOn { it > 3 }.allOf2(min2(), max2(), counter())
        )
        assertEquals(listOf(
                listOf(Optional.of(1), Optional.of(3), 4L),
                listOf(Optional.of(3), Optional.of(4), 3L),
                listOf(Optional.of(4), Optional.of(8), 4L)),
                actual)
    }

    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(
            allOf2(min2(), max2(), counter())
        )
        assertEquals(listOf(Optional.empty<Int>(), Optional.empty<Int>(), 0L), actual[0])
    }

    @Test
    fun handlesOneItem() {
        val element = 42
        val actual = listOf(element).consume(
            allOf2(min2(), max2(), counter())
        )
        assertEquals(listOf(Optional.of(element), Optional.of(element), 1L), actual[0])
    }

    @Test
    fun providesEmptyCopy() {
        val element = 42
        val nonEmptyConsumer = allOf2<Int>(min2(), max2(), counter())
        val actual = listOf(element).consume(
                nonEmptyConsumer
        )
        assertEquals(listOf(Optional.of(element), Optional.of(element), 1L), actual[0], "Guardian assumption: not empty")
        val sut = nonEmptyConsumer.emptyCopy()
        assertEquals(listOf(Optional.empty<Int>(), Optional.empty<Int>(), 0L), (sut.results() as List<Any>))
    }
}
