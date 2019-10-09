package com.tgt.trans.common.konsumers.resetters

import com.tgt.trans.common.konsumers.consumers.Consumer

class Resetter<T, V>(private val intermediateConsumerFactory: () -> Consumer<T>,
                     private val resetTrigger: IResetTrigger<T>,
                     private val intermediateResultsTransformer: (intermediateResults: Any, seriesDescription: Any) -> V,
                     private val finalConsumer: Consumer<V>,
                     val keepValueThatTriggeredReset: Boolean = false,
                     val repeatLastValueInNewSeries: Boolean = false): Consumer<T> {
    private var intermediateConsumer: Consumer<T> = intermediateConsumerFactory()
    private var previousValue: T? = null

    override fun process(value: T) {
        resetTrigger.process(value)
        if (resetTrigger.needsResetting()) {
            if (keepValueThatTriggeredReset) {
                intermediateConsumer.process(value)
                previousValue = value
            }

            processEndOfSeries()

            if (repeatLastValueInNewSeries) {
                if(keepValueThatTriggeredReset) {
                    submitToConsumerAndToResetTrigger(value)
                } else if(previousValue != null) {
                    submitToConsumerAndToResetTrigger(previousValue!!)
                }
            }

            if (!keepValueThatTriggeredReset) {
                submitToConsumerAndToResetTrigger(value)
            }
        } else {
            intermediateConsumer.process(value)
        }
        previousValue = value
    }

    private fun submitToConsumerAndToResetTrigger(value: T) {
        intermediateConsumer.process(value)
        resetTrigger.process(value)
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
                               finalConsumer: Consumer<V>,
                               keepValueThatTriggeredReset: Boolean = false,
                               repeatLastValueInNewSeries: Boolean = false): Consumer<T> =
    Resetter(intermediateConsumerFactory, resetTrigger, intermediateResultsTransformer, finalConsumer,
        keepValueThatTriggeredReset, repeatLastValueInNewSeries)

