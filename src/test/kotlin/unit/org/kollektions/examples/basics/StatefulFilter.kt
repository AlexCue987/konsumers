package org.kollektions.examples.basics

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.sumOfBigDecimal
import org.kollektions.transformations.filterOn
import org.kollektions.transformations.keepState
import org.kollektions.transformations.peek
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StatefulFilter {
    @Test
    fun `use state to prevent withdrawing more than account balance`() {
        val currentBalance = sumOfBigDecimal()
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

