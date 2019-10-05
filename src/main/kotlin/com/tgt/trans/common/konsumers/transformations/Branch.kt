package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder

class BranchConsumer<T>(val condition: (value: T) -> Boolean,
                      private val consumerForAccepted: Consumer<T>,
                      private val consumerForRejected: Consumer<T>) : Consumer<T> {

    override fun process(value: T) {
        if (condition(value)) {
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

class ChainedBranchConsumerBuilder<T>(val previousBuilder: ConsumerBuilder<T, T>,
                                       val condition: (value: T) -> Boolean,
                                       val consumerForRejected: Consumer<T>): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = previousBuilder.build(BranchConsumer(condition, innerConsumer, consumerForRejected))
}

class BranchConsumerBuilder<T>(val condition: (value: T) -> Boolean,
                             val consumerForRejected: Consumer<T>): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = BranchConsumer(condition, innerConsumer, consumerForRejected)
}

fun<T> ConsumerBuilder<T, T>.branchOn(condition: (value: T) -> Boolean, consumerForRejected: Consumer<T>) =
    ChainedBranchConsumerBuilder<T>(this, condition, consumerForRejected)

fun<T> branchOn(condition: (value: T) -> Boolean, consumerForRejected: Consumer<T>) = BranchConsumerBuilder(condition, consumerForRejected)
