package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.count
import com.tgt.trans.common.aggregator2.consumers.max
import com.tgt.trans.common.aggregator2.consumers.min
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class Statistics {
    @Test
    fun iterateOnceGetSeveralResults() {
        val actual = (1..10).asSequence()
                .consume(min(),
                    max(),
                    count())

        assertEquals(
                listOf(Optional.of(1),
                        Optional.of(10),
                        10L),
                actual)
    }
}
