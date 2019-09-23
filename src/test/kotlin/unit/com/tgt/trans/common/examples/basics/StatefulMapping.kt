package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StatefulMapping {
    @Test
    fun `convert deposits and withdrawals to account balance`() {
        val changes = listOf(BigDecimal.TEN, BigDecimal("-1"), BigDecimal("-1"), BigDecimal.ONE)
        val currentBalance = changes.consume(toCurrentBalance().asList())[0]
        print(currentBalance)
        assertEquals(listOf(BigDecimal.TEN, BigDecimal("9"), BigDecimal("8"), BigDecimal("9")), currentBalance)
    }
}
