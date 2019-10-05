package com.tgt.trans.common.examples

import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.count
import com.tgt.trans.common.konsumers.consumers.max
import com.tgt.trans.common.konsumers.consumers.min
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
