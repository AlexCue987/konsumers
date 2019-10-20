package com.tgt.trans.common.konsumers.consumers

class TopConsumer<T>(private val extremeValue: Int = -1,
                      private val comparator: (a:T, b:T) -> Int): Consumer<T> {
    constructor(comparator: (a:T, b:T) -> Int) : this(-1, comparator)

    private val items = mutableListOf<T>()

    override fun process(value: T) {
        val currentList = items
        if (items.isEmpty()) {
            items.add(value)
            return
        }
        when (comparator(items[0], value)) {
            0 -> {
                items.add(value)
                return
            }
            extremeValue -> {
                items.clear()
                items.add(value)
                return
            }
            else -> {}
        }
    }

    override fun results() = items

    override fun stop() {}
}

fun<T> topBy(comparator: (a:T, b:T) -> Int) =
    TopConsumer(comparator)

fun<T, F: Comparable<F>> topBy(projection: (a:T) -> F) =
    TopConsumer(comparator = { a:T, b:T -> projection(a).compareTo(projection(b)) })

fun<T, V> ConsumerBuilder<T, V>.topBy(comparator: (a:V, b:V) -> Int) =
    this.build(TopConsumer(comparator))

fun<T, V, F: Comparable<F>> ConsumerBuilder<T, V>.topBy(projection: (a:V) -> F) =
    this.build(TopConsumer(comparator = { a:V, b:V -> projection(a).compareTo(projection(b)) }))


fun<T> bottomNBy(comparator: (a:T, b:T) -> Int) =
    TopConsumer( 1, comparator)

fun<T, F: Comparable<F>> bottomNBy(projection: (a:T) -> F) =
    TopConsumer(1, comparator = { a:T, b:T -> projection(a).compareTo(projection(b)) })

fun<T, V> ConsumerBuilder<T, V>.bottomNBy(comparator: (a:V, b:V) -> Int) =
    this.build(TopConsumer(1, comparator))

fun<T, V, F: Comparable<F>> ConsumerBuilder<T, V>.bottomNBy(projection: (a:V) -> F) =
    this.build(TopConsumer(1, comparator = { a:V, b:V -> projection(a).compareTo(projection(b)) }))
