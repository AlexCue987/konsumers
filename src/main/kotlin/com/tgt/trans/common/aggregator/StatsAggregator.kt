package com.tgt.trans.common.aggregator

import java.util.*

class MinAggregator<T : Comparable<T>>: Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = MinAggregator()

    private var min: T? = null

    override fun process(value: T) {
        if (isEmpty() || min!! > value) min = value
    }

    override fun results(): Optional<T> {
        return if (isEmpty()) Optional.empty()
        else Optional.of(min!!)
    }

    override fun isEmpty() = (min == null)
}

fun<T : Comparable<T>> min() = MinAggregator<T>()


class MaxAggregator<T : Comparable<T>>: Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = MaxAggregator()

    private var max: T? = null

    override fun process(value: T) {
        if (isEmpty() || max!! < value) max = value
    }

    override fun results(): Optional<T> {
        return if (isEmpty()) Optional.empty()
        else Optional.of(max!!)
    }

    override fun isEmpty() = (max == null)
}

fun<T : Comparable<T>> max() = MaxAggregator<T>()


class CountAggregator<T>: Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = CountAggregator()

    private var count = 0L

    override fun process(value: T) {
        count++
    }

    override fun results(): Long {
        return count
    }

    override fun isEmpty() = (count == 0L)
}

fun<T> count() = CountAggregator<T>()
