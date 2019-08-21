package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.counter
import com.tgt.trans.common.aggregator2.consumers.max2
import com.tgt.trans.common.aggregator2.consumers.min2
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicExample {
    @Test
    fun iterateOnceGetSeveralResults() {
        val actual = (1..10).asSequence()
                .consume(min2(),
                    max2(),
                    counter())

        assertEquals(
                listOf(Optional.of(1),
                        Optional.of(10),
                        10L),
                actual)
    }
}

class BasicExample2 {
    @Test
    fun iterateOnceGetSeveralResults() {
        val actual = (1..10).asSequence()
            .consume(min2(),
                max2(),
                counter())

        assertEquals(
            listOf(Optional.of(1),
                Optional.of(10),
                10L),
            actual)
    }
}
