package com.tgt.trans.common.aggregator2.consumers

class TopNConsumer<T>(val count: Int,
                      private val comparator: (a:T, b:T) -> Int): Consumer<T> {
    private val items = (1..count).asIterable()
        .map {mutableListOf<T>()}
        .toMutableList()

    override fun process(value: T) {
        for (i in 0 until items.size) {
            val currentList = items[i]
            if (currentList.isEmpty()) {
                currentList.add(value)
                return
            }
            when (comparator(currentList[0], value)) {
                0 -> {
                    currentList.add(value)
                    return
                }
                -1 -> {
                    shiftSmallerItemsRight(i)
                    items[i] = mutableListOf(value)
                    return
                }
                1 -> {}
            }
        }
    }

    fun shiftSmallerItemsRight(index: Int) {
        for(i in (items.size-2) downTo index step 1) {
            items[i + 1] = items[i]
        }
    }

    override fun results() = items.filter { it.isNotEmpty() }
}

fun<T> topNBy(count: Int, comparator: (a:T, b:T) -> Int) =
    TopNConsumer(count, comparator)

fun<T, F: Comparable<F>> topNBy(count: Int, projection: (a:T) -> F) =
    TopNConsumer(count, comparator = { a:T, b:T -> projection(a).compareTo(projection(b)) })

fun<T, V> ConsumerBuilder<T, V>.topNBy(count: Int, comparator: (a:V, b:V) -> Int) =
    this.build(TopNConsumer(count, comparator))

fun<T, V, F: Comparable<F>> ConsumerBuilder<T, V>.topNBy(count: Int, projection: (a:V) -> F) =
    this.build(TopNConsumer(count, comparator = { a:V, b:V -> projection(a).compareTo(projection(b)) }))
