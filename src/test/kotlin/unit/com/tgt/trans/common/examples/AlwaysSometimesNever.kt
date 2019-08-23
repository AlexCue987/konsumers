package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.consumers.always
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.never
import com.tgt.trans.common.aggregator2.consumers.sometimes
import kotlin.test.assertEquals
import kotlin.test.Test

class AlwaysSometimesNever {
    @Test
    fun `condition sometimes met`() {
        val actual = listOf(1, -1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 }
        )
        assertEquals(listOf(false, false, true), actual)
    }
}
