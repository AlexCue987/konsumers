package com.tgt.trans.common.aggregator

class SeriesSplitter<T>(private val aggregator: Aggregator<List<T>>,
                     private val splitWhen: (series: List<T>, newValue: T) ->Boolean): Aggregator<T> {
    private var currentSeries = mutableListOf<T>()

    override fun process(value: T) {
        if(currentSeries.isNotEmpty() && splitWhen(currentSeries, value)) {
            endCurrentSeries()
        }
        currentSeries.add(value)
    }

    override fun results() = aggregator.results()

    override fun emptyCopy() = SeriesSplitter(aggregator.emptyCopy(), splitWhen)

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

fun<T> Aggregator<List<T>>.seriesSplit(filter: (series: List<T>, newValue: T) -> Boolean) = SeriesSplitter(this, filter)

