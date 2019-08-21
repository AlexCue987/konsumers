package com.tgt.trans.common.aggregator

class TopNAggregator<T>(private val limit: Int,
                        private val comparator: (a:T, b:T) -> Int ): Aggregator<T> {
    private val items = (1..limit).asIterable()
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

    override fun results() = items

    override fun emptyCopy(): Aggregator<T> = TopNAggregator(limit, comparator)

    override fun isEmpty() = items.all {  it.isEmpty() }
}

fun<T: Comparable<T>> topN(limit: Int) =
        TopNAggregator(limit) {a:T, b:T -> a.compareTo(b)}

fun<T, V: Comparable<V>> topNBy(limit: Int, expression: (value: T) -> V) =
        TopNAggregator(limit) {a:T, b:T -> expression(a).compareTo(expression(b))}