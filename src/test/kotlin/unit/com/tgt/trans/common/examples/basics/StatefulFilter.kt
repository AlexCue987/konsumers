package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.transformations.filterOn
import com.tgt.trans.common.konsumers.transformations.keepState
import com.tgt.trans.common.konsumers.transformations.peek
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StatefulFilter {
    @Test
    fun `use state to prevent withdrawing more than account balance`() {
        val currentBalance = com.tgt.trans.common.konsumers.consumers.sumOfBigDecimal()
        val changeToReject = BigDecimal("-2")
        val changes = listOf(BigDecimal("3"), BigDecimal("-2"), changeToReject, BigDecimal.ONE)
        val acceptedChanges = changes.consume(
            peek<BigDecimal> { println("Before filtering: $it, current balance : ${currentBalance.sum()}") }
                .filterOn { (currentBalance.sum() + it) >= BigDecimal.ZERO }
                .keepState(currentBalance)
                .peek { println("After filtering, change: $it, current balance: ${currentBalance.sum()}") }
                .asList()
        )[0]
        assertEquals(listOf(BigDecimal("3"), BigDecimal("-2"), BigDecimal.ONE), acceptedChanges)
    }
}

