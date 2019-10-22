package org.kollektions.dispatchers

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.ConsumerBuilder

class Branch<T>(val condition: (value: T) -> Boolean,
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

class ChainedBranchBuilder<T>(val previousBuilder: ConsumerBuilder<T, T>,
                              val condition: (value: T) -> Boolean,
                              val consumerForRejected: Consumer<T>): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = previousBuilder.build(Branch(condition, innerConsumer, consumerForRejected))
}

class BranchBuilder<T>(val condition: (value: T) -> Boolean,
                       val consumerForRejected: Consumer<T>): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = Branch(condition, innerConsumer, consumerForRejected)
}

fun<T> ConsumerBuilder<T, T>.branchOn(condition: (value: T) -> Boolean, consumerForRejected: Consumer<T>) =
    ChainedBranchBuilder<T>(this, condition, consumerForRejected)

fun<T> branchOn(condition: (value: T) -> Boolean, consumerForRejected: Consumer<T>) = BranchBuilder(condition, consumerForRejected)

fun<T> ConsumerBuilder<T, T>.branchOn(condition: (value: T) -> Boolean,
                                                                consumerForAccepted: Consumer<T>,
                                                                consumerForRejected: Consumer<T>) =
    this.build(Branch(condition, consumerForAccepted, consumerForRejected))
