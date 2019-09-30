package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.conditions.Condition
import com.tgt.trans.common.aggregator2.conditions.VanillaCondition
import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class BranchConsumer<T>(val condition: Condition<T>,
                      private val consumerForAccepted: Consumer<T>,
                      private val consumerForRejected: Consumer<T>) : Consumer<T> {

    override fun process(value: T) {
        if (condition[value]) {
            consumerForAccepted.process(value)
        } else {
            consumerForRejected.process(value)
        }
    }

    override fun results() = listOf(consumerForAccepted.results(), consumerForRejected.results())

    override fun stop() {
        consumerForAccepted.stop()
        consumerForRejected.stop()
    }
}

class ChainedBranchConsumerBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>,
                                       val condition: Condition<V>,
                                       val consumerForRejected: Consumer<V>): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(BranchConsumer(condition, innerConsumer, consumerForRejected))
}

class BranchConsumerBuilder<T>(val condition: Condition<T>,
                             val consumerForRejected: Consumer<T>): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = BranchConsumer(condition, innerConsumer, consumerForRejected)
}

fun<T, V> ConsumerBuilder<T, V>.branchOn(condition: Condition<V>, consumerForRejected: Consumer<V>) =
    ChainedBranchConsumerBuilder(this, condition, consumerForRejected)

fun<T> branchOn(branch: Condition<T>, consumerForRejected: Consumer<T>) = BranchConsumerBuilder(branch, consumerForRejected)

fun<T, V> ConsumerBuilder<T, V>.branchOn(condition: (a: V) -> Boolean, consumerForRejected: Consumer<V>) =
    ChainedBranchConsumerBuilder(this, VanillaCondition(condition), consumerForRejected)

fun<T> branchOn(condition: (a: T) -> Boolean, consumerForRejected: Consumer<T>) = BranchConsumerBuilder(VanillaCondition(condition), consumerForRejected)
