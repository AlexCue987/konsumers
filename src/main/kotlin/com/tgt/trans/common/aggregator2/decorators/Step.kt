package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class Step<T>(private val step: Int, private val innerConsumer: Consumer<T>): Consumer<T> {
    var itemsProcessed = 0

    init {
        require(step > 1) {"Step must be greater than 1, is: $step"}
    }

    override fun process(value: T) {
        if (itemsProcessed++ % step == (step - 1)) {
            innerConsumer.process(value)
        }
    }

    override fun results() = innerConsumer.results()

    override fun emptyCopy() = Step(step, innerConsumer.emptyCopy())

    override fun isEmpty() = innerConsumer.isEmpty()

    override fun stop() { innerConsumer.stop() }
}

class StepBuilder<T>(val count: Int): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = Step(count, innerConsumer)
}

class ChainedStepBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>,
                                   val count: Int): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Step(count, innerConsumer))
}

fun<T> step(count: Int) = StepBuilder<T>(count)

fun<T, V> ConsumerBuilder<T, V>.step(count: Int): ConsumerBuilder<T, V> = ChainedStepBuilder(this, count)
