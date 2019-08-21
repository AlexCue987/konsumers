package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.count
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupedConsumerTest {
    @Test
    fun groups() {
        val actual = listOf(1, 2, 3, 4, 5)
                .consume(groupBy { a: Int -> a % 2 }.count())
        assertEquals(mapOf(0 to 2L, 1 to 3L), actual[0])
    }

    @Test
    fun groupsAndFilters() {
        val actual = listOf(1, 2, 3, 4, 5)
                .consume(groupBy { a: Int -> a % 2 }
                        .filterOn { it < 4 }
                        .count())
        assertEquals(mapOf(0 to 1L, 1 to 2L), actual[0])
    }

    @Test
    fun mapsAndGroups() {
        val actual = listOf(1, 2, 3, 4, 5, 6, 7)
                .consume(mapTo { a: Int -> a % 5 }
                        .groupBy { it % 2 }
                        .count(),
                        groupBy { a: Int -> a % 2 }
                                .count())
        assertEquals(listOf(mapOf(0 to 4L, 1 to 3L),
                mapOf(0 to 3L, 1 to 4L)), actual)
    }
}

data class Thing(val color: String, val length: Int, val width: Int)
