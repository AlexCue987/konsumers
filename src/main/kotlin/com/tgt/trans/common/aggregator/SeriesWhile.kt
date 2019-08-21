package com.tgt.trans.common.aggregator

class SeriesWhile<T>(private val aggregator: Aggregator<List<T>>,
                     private val filter: (a: T) ->Boolean): Aggregator<T> {
    private var currentSeries = mutableListOf<T>()

    override fun process(value: T) {
        if(filter(value)) {
            currentSeries.add(value)
        } else {
            endCurrentSeries()
        }
    }

    override fun results() = aggregator.results()

    override fun emptyCopy() = SeriesWhile(aggregator.emptyCopy(), filter)

    override fun isEmpty() = aggregator.isEmpty() && currentSeries.isEmpty()

    override fun stop() {
        endCurrentSeries()
        aggregator.stop()
    }

    private fun endCurrentSeries() {
        if (currentSeries.isNotEmpty()) {
            aggregator.process(currentSeries.toList())
            currentSeries.clear()
        }
    }
}

fun<T> Aggregator<List<T>>.seriesWhile(filter: (a: T) -> Boolean) = SeriesWhile(this, filter)

