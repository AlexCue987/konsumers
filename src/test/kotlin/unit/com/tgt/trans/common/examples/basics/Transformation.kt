package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder

class TransformationConsumer<T, V>(private val condition: (value: T) -> Boolean,
                                       private val transformation: (value: T) -> Sequence<V>,
                                       private val innerConsumer: Consumer<V>): Consumer<T> {
    override fun process(value: T) {
        if (condition(value)) {
            transformation(value).forEach { innerConsumer.process(it) }
        }
    }

    override fun results() = innerConsumer.results()
}

class TransformationConsumerBuilder<T, V>(private val condition: (value: T) -> Boolean,
                                          private val transformation: (value: T) -> Sequence<V>): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = TransformationConsumer(condition, transformation, innerConsumer)
}

class ChainedTransformationConsumerBuilder<T, V, W>(private val previousBuilder: ConsumerBuilder<T, V>,
                                                    private val condition: (value: V) -> Boolean,
                                                    private val transformation: (value: V) -> Sequence<W>): ConsumerBuilder<T, W> {
    override fun build(innerConsumer: Consumer<W>): Consumer<T> = previousBuilder.build(TransformationConsumer(condition, transformation, innerConsumer))
}

fun<T, V> transformTo(condition: (value: T) -> Boolean,
                transformation: (value: T) -> Sequence<V>) = TransformationConsumerBuilder(condition, transformation)

fun<T, V, W> ConsumerBuilder<T, V>.transformTo(condition: (value: V) -> Boolean,
                                         transformation: (value: V) -> Sequence<W>): ConsumerBuilder<T, W> = ChainedTransformationConsumerBuilder(this, condition, transformation)
