package com.tgt.trans.common.aggregator

class ItemsAggregator<T>: Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = ItemsAggregator()

    private var items = mutableListOf<T>()

    override fun process(value: T) {
        items.add(value)
    }

    override fun results() = items.toList()

    override fun isEmpty() = items.isEmpty()
}

fun<T> takeAll() = ItemsAggregator<T>()

class FirstItemsAggregator<T>(private val limit: Int): Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = FirstItemsAggregator(limit)

    private var items = mutableListOf<T>()

    override fun process(value: T) {
        if(items.size < limit) {
            items.add(value)
        }
    }

    override fun results() = items

    override fun isEmpty() = items.isEmpty()
}

fun<T> takeFirst(limit: Int) = FirstItemsAggregator<T>(limit)

class LastItemsAggregator<T>(private val limit: Int): Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = LastItemsAggregator(limit)

    private var roundRobinBuffer = mutableListOf<T>()
    private var offset = -1

    override fun process(value: T) {
        offset++
        if(isFull()) {
            roundRobinBuffer[offset % limit] = value
        } else {
            roundRobinBuffer.add(value)
        }
    }

    private fun isFull() = offset >= limit

    override fun results() = if(isFull()) {
        ((offset + 1)..(offset + limit)).asSequence()
                .map { roundRobinBuffer[it % limit] }
                .toList()
    } else {
        roundRobinBuffer.toList()
    }

    override fun isEmpty() = roundRobinBuffer.isEmpty()
}

fun<T> takeLast(limit: Int) = LastItemsAggregator<T>(limit)

class SomeItemsAggregator<T>(private val start: Int=0, private val count: Int, private val step: Int = 1): Aggregator<T> {
    var index = -1
    override fun emptyCopy(): Aggregator<T> = SomeItemsAggregator(start, count, step)

    private var items = mutableListOf<T>()

    override fun process(value: T) {
        if(items.size < count && ++index >= start) {
            if((index - start) % step == 0) {
                items.add(value)
            }
        }
    }

    override fun results() = items

    override fun isEmpty() = items.isEmpty()
}

fun<T> takeSome(start: Int=0, count: Int, step: Int = 1) = SomeItemsAggregator<T>(start, count, step)
