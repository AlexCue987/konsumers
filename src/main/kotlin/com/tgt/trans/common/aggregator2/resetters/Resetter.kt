package com.tgt.trans.common.aggregator2.resetters

import com.tgt.trans.common.aggregator2.consumers.Consumer

class Resetter<T, V>(private var intermediateConsumer: Consumer<T>,
                     private val resetTrigger: ResetTrigger<T>,
                     private val intermediateResultsTransformer: (intermediateResults: Any, seriesDescription: Any) -> V,
                     private val finalConsumer: Consumer<V>): Consumer<T> {

    override fun process(value: T) {
        resetTrigger.process(value)
        if (resetTrigger.needsResetting()) {
            if (resetTrigger.keepValueThatTriggeredReset) {
                intermediateConsumer.process(value)
                processEndOfSeries()
            } else {
                processEndOfSeries()
                intermediateConsumer.process(value)
            }
        } else {
            intermediateConsumer.process(value)
        }
    }

    private fun processEndOfSeries() {
        intermediateConsumer.stop()
        if (!intermediateConsumer.isEmpty()) {
            val transformedResults = intermediateResultsTransformer(intermediateConsumer.results(),
                resetTrigger.describeSeries())
            finalConsumer.process(transformedResults)
        }
        resetTrigger.seriesReset()
        intermediateConsumer = intermediateConsumer.emptyCopy()
    }

    override fun results() = finalConsumer.results()

    override fun emptyCopy() = Resetter(intermediateConsumer.emptyCopy(),
        resetTrigger,
        intermediateResultsTransformer,
        finalConsumer.emptyCopy())

    override fun isEmpty() = intermediateConsumer.isEmpty() && finalConsumer.isEmpty()

    override fun stop() {
        processEndOfSeries()
    }
}

interface ResetTrigger<T> {
    fun process(value: T)
    fun needsResetting(): Boolean
    fun describeSeries(): Any
    val keepValueThatTriggeredReset: Boolean
    fun seriesReset()
}

fun<T, V> Consumer<T>.withResetting(resetTrigger: ResetTrigger<T>,
                                                                               intermediateResultsTransformer: (a: Any, b: Any) -> V,
                                                                               finalConsumer: Consumer<V>): Consumer<T> =
    Resetter(this, resetTrigger, intermediateResultsTransformer, finalConsumer)

