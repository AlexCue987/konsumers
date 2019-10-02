package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.conditions.State
import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.keepState
import com.tgt.trans.common.aggregator2.decorators.peek
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StatefulFilter {
    @Test
    fun `use state to prevent withdrawing more than account balance`() {
        val currentBalance = com.tgt.trans.common.aggregator2.consumers.sumOfBigDecimal()
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

class TransformationWithState<T, V, O>(private val state: State<T, V>,
                                    private val condition: (stateValue: V, incomingValue: T) -> Boolean,
                                    private val transformation: (stateValue: V, incomingValue: T) -> Sequence<O>,
                                    private val innerConsumer: Consumer<O>): Consumer<T> {
    override fun process(incomingValue: T) {
        if (condition(state.stateValue(), incomingValue)) {
            state.process(incomingValue)
            transformation(state.stateValue(), incomingValue).forEach { innerConsumer.process(it) }
        }
    }

    override fun results() = innerConsumer.results()
}


class TransformationWithStateOfIncoming<T, V, O>(private val state: State<T, V>,
                                       private val condition: (stateValue: V, incomingValue: T) -> Boolean,
                                       private val transformation: (stateValue: V, incomingValue: T) -> Sequence<O>,
                                       private val innerConsumer: Consumer<O>): Consumer<T> {
    override fun process(incomingValue: T) {
        state.process(incomingValue)
        if (condition(state.stateValue(), incomingValue)) {
            transformation(state.stateValue(), incomingValue).forEach { innerConsumer.process(it) }
        }
    }

    override fun results() = innerConsumer.results()
}

class Aggregate<T>(initialValue: T,
                   private val aggregator: (a: T, b: T) -> T): State<T, T> {
    private var aggregate = initialValue

    override fun process(value: T) {
        aggregate = aggregator(aggregate, value)
    }

    override fun stateValue() = aggregate
}

fun sumOfBigDecimal(): Aggregate<BigDecimal> = Aggregate(initialValue = BigDecimal.ZERO) { a: BigDecimal, b: BigDecimal -> a + b}


class CurrentBalanceConsumerBuilder(): ConsumerBuilder<BigDecimal, BigDecimal> {
    override fun build(innerConsumer: Consumer<BigDecimal>): Consumer<BigDecimal> = TransformationWithState(state = sumOfBigDecimal(),
        condition = { currentSum: BigDecimal, change: BigDecimal -> (currentSum + change) >= BigDecimal.ZERO},
        transformation = {stateValue: BigDecimal, incomingValue: BigDecimal -> sequenceOf(stateValue)},
        innerConsumer = innerConsumer
    )
}

fun toCurrentBalance() = CurrentBalanceConsumerBuilder()


