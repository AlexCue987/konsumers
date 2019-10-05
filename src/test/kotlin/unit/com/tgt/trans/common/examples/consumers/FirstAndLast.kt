package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.konsumers.consumers.FirstN
import com.tgt.trans.common.konsumers.consumers.LastN
import com.tgt.trans.common.konsumers.consumers.consume
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstAndLast {
    @Test
    fun `first and last N`() {
        val actual = (1..10).asSequence()
            .consume(FirstN(2), LastN(2))

        print(actual)

        assertEquals(listOf(listOf(1, 2), listOf(9, 10)), actual)
    }
}
