package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.konsumers.consumers.always
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.never
import com.tgt.trans.common.konsumers.consumers.sometimes
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
        print(actual)
        assertEquals(listOf(false, false, true), actual)
    }
}
