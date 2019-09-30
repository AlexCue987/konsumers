package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.conditions.Condition
import com.tgt.trans.common.aggregator2.conditions.VanillaCondition
import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class FilterConsumer<T>(val filter: Condition<T>, val innerConsumer: Consumer<T>) : Consumer<T> {

    override fun process(value: T) {
        if (filter[value]) {
            innerConsumer.process(value)
        }
    }

    override fun results() = innerConsumer.results()

    override fun stop() {
        innerConsumer.stop()
    }
}

class ChainedFilterConsumerBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>, val filter: Condition<V>): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(FilterConsumer(filter, innerConsumer))
}

class FilterConsumerBuilder<T>(val filter: Condition<T>): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = FilterConsumer(filter, innerConsumer)
}

fun<T, V> ConsumerBuilder<T, V>.filterOn(filter: Condition<V>) = ChainedFilterConsumerBuilder(this, filter)

fun<T> filterOn(filter: Condition<T>) = FilterConsumerBuilder(filter)

fun<T, V> ConsumerBuilder<T, V>.filterOn(filter: (a: V) -> Boolean) = ChainedFilterConsumerBuilder(this, VanillaCondition(filter))

fun<T> filterOn(filter: (a: T) -> Boolean) = FilterConsumerBuilder(VanillaCondition(filter))
