package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder

class Stopper<T>(private val innerConsumer: Consumer<T>, 
                 private val keepBreakingPoint: Boolean,
                 val condition: (a:T) -> Boolean ): Consumer<T> {
    var conditionMet = false

    override fun process(value: T) {
        if (conditionMet) {
            return
        }

        conditionMet = condition(value)
        if (!conditionMet || keepBreakingPoint) {
            innerConsumer.process(value)
        }
    }

    override fun results() = innerConsumer.results()

    override fun stop() = innerConsumer.stop()
}

class StopperBuilder<T>(private val keepBreakingPoint: Boolean,
                        private val condition: (a:T) -> Boolean): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = Stopper(innerConsumer, keepBreakingPoint, condition)
}

class ChainedStopperBuilder<T, V>(private val previousBuilder: ConsumerBuilder<T, V>,
                                  private val keepBreakingPoint: Boolean,
                                  private val condition: (a:V) -> Boolean): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Stopper<V>(innerConsumer,
        keepBreakingPoint, condition))
}

fun<T> stopWhen(condition: (a:T) -> Boolean) =
    StopperBuilder(true, condition)

fun<T> stopAfter(condition: (a:T) -> Boolean) =
    StopperBuilder(false, condition)

fun<T, V> ConsumerBuilder<T, V>.stopWhen(condition: (a:V) -> Boolean): ConsumerBuilder<T, V> =
    ChainedStopperBuilder(this, true, condition)

fun<T, V> ConsumerBuilder<T, V>.stopAfter(condition: (a:V) -> Boolean): ConsumerBuilder<T, V> =
    ChainedStopperBuilder(this, false, condition)
