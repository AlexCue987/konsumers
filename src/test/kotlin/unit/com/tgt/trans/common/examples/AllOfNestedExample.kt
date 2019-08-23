package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.max
import com.tgt.trans.common.aggregator2.consumers.min
import com.tgt.trans.common.aggregator2.decorators.allOf
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test

class AllOfNestedExample {
    @Test
    fun decorateConsumers() {
        val actual = (1..10).asSequence()
            .consume(
                filterOn<Int> { it > 2 }
                    .allOf(
                        mapTo<Int, Int> { it * 2 }
                            .allOf(min(), max()),
                        mapTo<Int, Int> { it * 3 }
                            .allOf(min(), max())
            ))

        assertEquals(
            listOf(
                listOf(Optional.of(6), Optional.of(20)),
                listOf(Optional.of(9), Optional.of(30))
            ),
            actual[0])
    }
}
