package com.tgt.trans.common.aggregator

class Batcher<T> (private val batchSize: Int,
                  private val consumer: Aggregator<List<T>>): Aggregator<T> {
    val currentBatch = mutableListOf<T>()

    override fun process(value: T) {
        currentBatch.add(value)
        if (currentBatch.size == batchSize) {
            consumer.process(currentBatch.toList())
            currentBatch.clear()
        }
    }

    override fun results() = consumer.results()

    override fun emptyCopy() = Batcher(batchSize, consumer.emptyCopy())

    override fun isEmpty() = consumer.isEmpty() && currentBatch.isEmpty()

    override fun stop() {
        if (currentBatch.isNotEmpty()) {
            consumer.process(currentBatch.toList())
        }
        consumer.stop()
    }
}

fun<T> Aggregator<List<T>>.batches(batchSize: Int) = Batcher(batchSize, this)
