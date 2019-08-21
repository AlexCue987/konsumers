package com.tgt.trans.common.aggregator

class WithPreceding<T>(
        private val aggregator: Aggregator<List<T>>,
        private val limit: Int,
        private val allowPartialLength: Boolean): Aggregator<T> {
    private val buffer = mutableListOf<T>()

    override fun process(value: T) {
        buffer.add(value)
        if (buffer.size == limit || allowPartialLength) {
            aggregator.process(buffer.toList())
        }
        if (buffer.size == limit) {
            buffer.removeAt(0)
        }
    }

    override fun results() = aggregator.results()

    override fun emptyCopy() = WithPreceding(aggregator.emptyCopy(), limit, allowPartialLength)

    override fun isEmpty() = aggregator.isEmpty()

    override fun stop() = aggregator.stop()
}

fun<T> Aggregator<List<T>>.withPrecedingExactly(limit: Int) = WithPreceding(this, limit, false)

fun<T> Aggregator<List<T>>.withPrecedingAtMost(limit: Int) = WithPreceding(this, limit, true)

