package com.tgt.trans.common.examples.transformations

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.transformations.peek
import kotlin.test.Test

class PeekExample {
    @Test
    fun `peek example`() {
        (0..3).asSequence().consume(
            peek<Int> { println("Processing item $it") }.asList()
        )
    }
}
