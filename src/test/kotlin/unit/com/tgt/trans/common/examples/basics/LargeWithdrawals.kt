package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.keepState
import com.tgt.trans.common.aggregator2.decorators.mapTo
import com.tgt.trans.common.aggregator2.decorators.peek
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class LargeWithdrawals {
    private val expected = listOf(TransactionWithCurrentBalance(currentBalance=BigDecimal(39), amount=BigDecimal(-50)))

    private val amountsToProcess = listOf(BigDecimal(100), BigDecimal(-10), BigDecimal(-1), BigDecimal(-50))

    @Test
    fun `creates many short-lived objects`() {
        val amounts = listOf(BigDecimal(100), BigDecimal(-10), BigDecimal(-1), BigDecimal(-50))
        val largeWithdrawals = amounts.consume(toTransactionWithCurrentBalance()
            .peek { println("Before filtering: $it") }
            .filterOn { -it.amount > it.currentBalance * BigDecimal("0.5") }
            .peek { println("After filtering: $it") }
            .asList())
        assertEquals(expected, largeWithdrawals[0])
    }

    @Test
    fun `does not create short-lived objects, maps and filters in one step`() {
        val amounts = listOf(BigDecimal(100), BigDecimal(-10), BigDecimal(-1), BigDecimal(-50))
        val largeWithdrawals = amounts.consume(toLargeWithdrawal()
            .peek { println("After mapping and filtering: $it") }
            .asList())
        assertEquals(expected, largeWithdrawals[0])
    }

    @Test
    fun `creates many short-lived objects v2`() {
        val currentBalance = com.tgt.trans.common.aggregator2.consumers.sumOfBigDecimal()
        val largeWithdrawals = amountsToProcess.consume(
            keepState(currentBalance)
                .peek { println("Before filtering and mapping: item $it, currentBalance ${currentBalance.sum()}") }
                .mapTo { TransactionWithCurrentBalance(currentBalance.sum(), it) }
                .peek { println("After mapping, before filtering: $it") }
                .filterOn { -it.amount > it.currentBalance * BigDecimal("0.5") }
                .peek { println("After filtering: $it") }
                .asList())
        assertEquals(expected, largeWithdrawals[0])
    }

    @Test
    fun `does not create short-lived objects, maps and filters in one step v2`() {
        val currentBalance = com.tgt.trans.common.aggregator2.consumers.sumOfBigDecimal()
        val amounts = listOf(BigDecimal(100), BigDecimal(-10), BigDecimal(-1), BigDecimal(-50))
        val largeWithdrawals = amounts.consume(
            keepState(currentBalance)
                .peek { println("Before filtering and mapping: item $it, currentBalance ${currentBalance.sum()}") }
                .transformTo(condition = {value:BigDecimal -> -value > currentBalance.sum() * BigDecimal("0.5") },
                    transformation = {value:BigDecimal -> sequenceOf(TransactionWithCurrentBalance(currentBalance.sum(), value))}
                    )
            .peek { println("After mapping and filtering: $it") }
            .asList())
        assertEquals(expected, largeWithdrawals[0])
    }
}

data class TransactionWithCurrentBalance(val currentBalance: BigDecimal, val amount: BigDecimal)

class TransactionWithCurrentBalanceConsumerBuilder(): ConsumerBuilder<BigDecimal, TransactionWithCurrentBalance> {
    override fun build(innerConsumer: Consumer<TransactionWithCurrentBalance>): Consumer<BigDecimal> = TransformationWithState(state = sumOfBigDecimal(),
        condition = { currentSum: BigDecimal, change: BigDecimal -> true },
        transformation = {stateValue: BigDecimal, incomingValue: BigDecimal -> sequenceOf(TransactionWithCurrentBalance(stateValue, incomingValue))},
        innerConsumer = innerConsumer
    )
}

fun toTransactionWithCurrentBalance() = TransactionWithCurrentBalanceConsumerBuilder()

class LargeWithdrawalConsumerBuilder(): ConsumerBuilder<BigDecimal, TransactionWithCurrentBalance> {
    override fun build(innerConsumer: Consumer<TransactionWithCurrentBalance>): Consumer<BigDecimal> = TransformationWithStateOfIncoming(state = sumOfBigDecimal(),
        condition = { currentSum: BigDecimal, change: BigDecimal -> (currentSum + change) < currentSum * BigDecimal("0.5")},
        transformation = {stateValue: BigDecimal, incomingValue: BigDecimal -> sequenceOf(TransactionWithCurrentBalance(stateValue, incomingValue))},
        innerConsumer = innerConsumer
    )
}

fun toLargeWithdrawal() = LargeWithdrawalConsumerBuilder()
