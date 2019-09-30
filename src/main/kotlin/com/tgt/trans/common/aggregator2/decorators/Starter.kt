package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class Starter<T>(private val innerConsumer: Consumer<T>, 
                 private val keepBreakingPoint: Boolean,
                 val condition: (a:T) -> Boolean ): Consumer<T> {
    var conditionMet = false

    override fun process(value: T) {
        if (!conditionMet) {
            conditionMet = condition(value)
            if (!conditionMet || !keepBreakingPoint) {
                return
            }
        }

        innerConsumer.process(value)
    }

    override fun results() = innerConsumer.results()

    override fun stop() = innerConsumer.stop()
}

class StarterBuilder<T>(private val keepBreakingPoint: Boolean,
                        private val condition: (a:T) -> Boolean): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = Starter(innerConsumer, keepBreakingPoint, condition)
}

class ChainedStarterBuilder<T, V>(private val previousBuilder: ConsumerBuilder<T, V>,
                                  private val keepBreakingPoint: Boolean,
                                  private val condition: (a:V) -> Boolean): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Starter<V>(innerConsumer,
        keepBreakingPoint, condition))
}

fun<T> startWhen(condition: (a:T) -> Boolean) =
    StarterBuilder(true, condition)

fun<T> startAfter(condition: (a:T) -> Boolean) =
    StarterBuilder(false, condition)

fun<T, V> ConsumerBuilder<T, V>.startWhen(condition: (a:V) -> Boolean): ConsumerBuilder<T, V> =
    ChainedStarterBuilder(this, true, condition)

fun<T, V> ConsumerBuilder<T, V>.startAfter(condition: (a:V) -> Boolean): ConsumerBuilder<T, V> =
    ChainedStarterBuilder(this, false, condition)
