package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder

class TransformationConsumer<T, V>(private val transformation: (value: T) -> Sequence<V>,
                                       private val innerConsumer: Consumer<V>): Consumer<T> {
    override fun process(value: T) {
        transformation(value).forEach { innerConsumer.process(it) }
    }

    override fun results() = innerConsumer.results()

    override fun stop() = innerConsumer.stop()
}

class TransformationConsumerBuilder<T, V>(private val transformation: (value: T) -> Sequence<V>): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = TransformationConsumer(transformation, innerConsumer)
}

class ChainedTransformationConsumerBuilder<T, V, W>(private val previousBuilder: ConsumerBuilder<T, V>,
                                                    private val transformation: (value: V) -> Sequence<W>): ConsumerBuilder<T, W> {
    override fun build(innerConsumer: Consumer<W>): Consumer<T> = previousBuilder.build(TransformationConsumer(transformation, innerConsumer))
}

fun<T, V> transformTo(transformation: (value: T) -> Sequence<V>) = TransformationConsumerBuilder(transformation)

fun<T, V, W> ConsumerBuilder<T, V>.transformTo(transformation: (value: V) -> Sequence<W>): ConsumerBuilder<T, W> = ChainedTransformationConsumerBuilder(this, transformation)
