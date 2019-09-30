package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class Batcher2<T> (private val batchSize: Int,
                  private val consumer: Consumer<List<T>>): Consumer<T> {
    val currentBatch = mutableListOf<T>()

    override fun process(value: T) {
        currentBatch.add(value)
        if (currentBatch.size == batchSize) {
            consumer.process(currentBatch.toList())
            currentBatch.clear()
        }
    }

    override fun results() = consumer.results()

    override fun stop() {
        if (currentBatch.isNotEmpty()) {
            consumer.process(currentBatch.toList())
        }
        consumer.stop()
    }
}

class ChainedBatcherConsumerBuilder<T, V>(private val previousBuilder: ConsumerBuilder<T, V>,
                                          private val batchSize: Int): ConsumerBuilder<T, List<V>> {
    override fun build(innerConsumer: Consumer<List<V>>): Consumer<T> = previousBuilder.build(Batcher2(batchSize, innerConsumer))
}

class BatcherConsumerBuilder<T>(private val batchSize: Int): ConsumerBuilder<T, List<T>> {
    override fun build(innerConsumer: Consumer<List<T>>): Consumer<T> = Batcher2(batchSize, innerConsumer)
}

fun<T, V> ConsumerBuilder<T, V>.batches(batchSize: Int) = ChainedBatcherConsumerBuilder(this, batchSize)

fun<T> batches(batchSize: Int) = BatcherConsumerBuilder<T>(batchSize)
