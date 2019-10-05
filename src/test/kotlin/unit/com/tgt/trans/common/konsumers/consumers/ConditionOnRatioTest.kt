package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.mapTo
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.Test

class ConditionOnRatioTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 })
        assertEquals(listOf(true, true, false), actual)
    }

    @Test
    fun handlesOneItemConditionNotMet() {
        val actual = listOf(-1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 })
        assertEquals(listOf(true, false, false), actual)
    }

    @Test
    fun handlesOneItemConditionMet() {
        val actual = listOf(1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 })
        assertEquals(listOf(false, true, true), actual)
    }

    @Test
    fun handlesSeveralItemsConditionSometimesMet() {
        val actual = listOf(1, -1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 })
        assertEquals(listOf(false, false, true), actual)
    }

    @Test
    fun chainsWithBuilder() {
        val projection = mapTo { a: Int -> BigDecimal(a.toLong()).times(BigDecimal("0.5")) }
        val actual = listOf(1, 2, 3).consume(projection.never {it > BigDecimal.ONE},
            projection.always {it > BigDecimal.ONE},
            projection.sometimes {it > BigDecimal.ONE})
        assertEquals(listOf(false, false, true), actual)
    }
}
