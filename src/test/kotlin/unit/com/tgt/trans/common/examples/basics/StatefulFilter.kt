package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.conditions.Condition
import com.tgt.trans.common.aggregator2.conditions.State
import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.keepState
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StatefulFilter {
    @Test
    fun `prevent withdrawing more than account balance`() {
        val changeToReject = BigDecimal("-2")
        val changes = listOf(BigDecimal.ONE, BigDecimal("-1"), changeToReject, BigDecimal.ONE)
        val condition = nonNegativeBalance()
        val acceptedChanges = changes.consume(filterOn(condition).asList())[0]
        print(acceptedChanges)
        assertEquals(listOf(BigDecimal.ONE, BigDecimal("-1"), BigDecimal.ONE), acceptedChanges)
    }

//    @Test
//    fun `use state to prevent withdrawing more than account balance`() {
//        val changeToReject = BigDecimal("-2")
//        val changes = listOf(BigDecimal.ONE, BigDecimal("-1"), changeToReject, BigDecimal.ONE)
//        val condition = nonNegativeBalance()
//        val currentBalance = sumOfBigDecimal()
//        val acceptedChanges = changes.consume(filterOn(condition)
//            .keepState(currentBalance)
//            .asList())[0]
//        print(acceptedChanges)
//        assertEquals(listOf(BigDecimal.ONE, BigDecimal("-1"), BigDecimal.ONE), acceptedChanges)
//    }
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

class ConditionOnState<T, V>(private val state: State<T, V>,
                          private val condition: (stateValue: V, incomingValue: T) -> Boolean): Condition<T>{
    override fun get(incomingValue: T): Boolean {
        val accepted = condition(state.stateValue(), incomingValue)
        if(accepted) {
            state.process(incomingValue)
        }
        return accepted
    }

    override fun emptyCopy(): Condition<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun sumAfterChangeIsNonNegative(sum: BigDecimal, change: BigDecimal) = (sum + change) >= BigDecimal.ZERO

fun nonNegativeBalance(): Condition<BigDecimal> =
    ConditionOnState(state = sumOfBigDecimal()) { currentSum: BigDecimal, change: BigDecimal -> (currentSum + change) >= BigDecimal.ZERO }


class CurrentBalanceConsumerBuilder(): ConsumerBuilder<BigDecimal, BigDecimal> {
    override fun build(innerConsumer: Consumer<BigDecimal>): Consumer<BigDecimal> = TransformationWithState(state = sumOfBigDecimal(),
        condition = { currentSum: BigDecimal, change: BigDecimal -> (currentSum + change) >= BigDecimal.ZERO},
        transformation = {stateValue: BigDecimal, incomingValue: BigDecimal -> sequenceOf(stateValue)},
        innerConsumer = innerConsumer
    )
}

fun toCurrentBalance() = CurrentBalanceConsumerBuilder()


