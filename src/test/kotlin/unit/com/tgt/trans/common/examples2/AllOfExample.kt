package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.max2
import com.tgt.trans.common.aggregator2.consumers.min2
import com.tgt.trans.common.aggregator2.decorators.allOf2
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test

class AllOfExample {
    @Test
    fun decorateConsumers() {
        val actual = (1..10).asSequence()
                .consume(
                        filterOn<Int> { it > 2 }
                                .mapTo { it * 2 }
                                .allOf2(min2(), max2()))

        assertEquals(
                listOf(
                        listOf(Optional.of(6), Optional.of(20))),
                actual)
    }
}
