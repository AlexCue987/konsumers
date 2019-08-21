package com.tgt.trans.common.aggregator

class PairsFactory<T> (private val consumer: Aggregator<Pair<T, T>>): Aggregator<T> {
    var previous: T? = null
    var current: T? = null

    override fun process(value: T) {
        previous = current
        current = value
        if (previous != null) {
            consumer.process(Pair(previous!!, current!!))
        }
    }

    override fun results() = consumer.results()

    override fun emptyCopy() = PairsFactory(consumer.emptyCopy())

    override fun isEmpty() = consumer.isEmpty()

    override fun stop() = consumer.stop()
}

fun<T> Aggregator<Pair<T, T>>.pairs() = PairsFactory(this)
