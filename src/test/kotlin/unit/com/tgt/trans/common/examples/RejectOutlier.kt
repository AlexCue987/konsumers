package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.conditions.lastItemCondition
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.filterOn
import kotlin.test.Test
import kotlin.test.assertEquals

class RejectOutlier {
    val sut = lastItemCondition<Int>({ a: Int, b: Int -> Math.abs(a - b) < 30 })

    @Test
    fun `rejects outlier`() {
        val outlier = 97
        val actual = listOf(55, 80, 52, outlier, 65).consume(filterOn(sut).asList())
        assertEquals(listOf(55, 80, 52, 65), actual[0])
    }
}
