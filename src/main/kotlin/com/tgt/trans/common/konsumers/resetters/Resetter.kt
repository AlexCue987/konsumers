package com.tgt.trans.common.konsumers.resetters

import com.tgt.trans.common.konsumers.consumers.Consumer

class Resetter<T, V>(private val intermediateConsumerFactory: () -> Consumer<T>,
                     private val resetTrigger: IResetTrigger<T>,
                     private val intermediateResultsTransformer: (intermediateResults: Any, seriesDescription: Any) -> V,
                     private val finalConsumer: Consumer<V>): Consumer<T> {
    private var intermediateConsumer: Consumer<T> = intermediateConsumerFactory()

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
        val transformedResults = intermediateResultsTransformer(intermediateConsumer.results(),
            resetTrigger.describeSeries())
        finalConsumer.process(transformedResults)
        resetTrigger.resetState()
        intermediateConsumer = intermediateConsumerFactory()
    }

    override fun results() = finalConsumer.results()

    override fun stop() {
        processEndOfSeries()
    }
}

fun<T, V> consumeWithResetting(intermediateConsumerFactory: () -> Consumer<T>,
                               resetTrigger: IResetTrigger<T>,
                               intermediateResultsTransformer: (a: Any, b: Any) -> V,
                               finalConsumer: Consumer<V>): Consumer<T> =
    Resetter(intermediateConsumerFactory, resetTrigger, intermediateResultsTransformer, finalConsumer)

