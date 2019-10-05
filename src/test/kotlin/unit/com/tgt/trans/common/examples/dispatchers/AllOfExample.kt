package com.tgt.trans.common.examples.dispatchers

import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.max
import com.tgt.trans.common.konsumers.consumers.min
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.transformations.filterOn
import com.tgt.trans.common.konsumers.transformations.mapTo
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test

class AllOfExample {
    @Test
    fun `allOf after transformations`() {
        val actual = (1..10).asSequence()
            .consume(
                filterOn<Int> { it > 2 }
                    .mapTo { it * 2 }
                    .allOf(min(), max()))

        assertEquals(
            listOf(
                listOf(Optional.of(6), Optional.of(20))),
            actual)
    }
}
