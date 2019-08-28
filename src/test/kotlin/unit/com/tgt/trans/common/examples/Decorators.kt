package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.count
import com.tgt.trans.common.aggregator2.consumers.max
import com.tgt.trans.common.aggregator2.consumers.min
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.groupBy
import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test

class Decorators {
    @Test
    fun decorateConsumers() {
        val actual = (1..10).asSequence()
                .consume(
                        filterOn<Int> { it > 2 }
                                .min(),
                        mapTo<Int, Int> { it * 2 }
                                .max(),
                        groupBy { a: Int -> a % 3 }
                                .count())

        assertEquals(
                listOf(
                        Optional.of(3),
                        Optional.of(20),
                        mapOf(0 to 3L, 1 to 4L, 2 to 3L)),
                actual)
    }
}