package com.tgt.trans.common.konsumers.dispatchers

import com.tgt.trans.common.konsumers.consumers.Consumer

class Resetter<T, V>(private val intermediateConsumersFactory: () -> List<Consumer<T>>,
                     private val resetTrigger: (intermediateConsumers: List<Consumer<T>>, value: T) -> Boolean,
                     private val intermediateResultsTransformer: (intermediateConsumers: List<Consumer<T>>) -> V,
                     private val finalConsumer: Consumer<V>,
                     private val keepValueThatTriggeredReset: Boolean = false,
                     private val repeatLastValueInNewSeries: Boolean = false): Consumer<T> {
    private var intermediateConsumers: List<Consumer<T>> = intermediateConsumersFactory()
    private var previousValue: T? = null
    private var empty = true

    override fun process(value: T) {
        if (resetTrigger(intermediateConsumers, value)) {
            if (keepValueThatTriggeredReset) {
                processByConsumers(value)
                previousValue = value
            }

            processEndOfSeries()

            if (repeatLastValueInNewSeries) {
                val lastValue = if(keepValueThatTriggeredReset) value else previousValue
                processByConsumers(lastValue!!)
            }

            if (!keepValueThatTriggeredReset) {
                processByConsumers(value)
            }
        } else {
            processByConsumers(value)
        }
        previousValue = value
    }

    private fun processByConsumers(value: T) {
        intermediateConsumers.forEach { it.process(value) }
        empty = false
    }

    private fun processEndOfSeries() {
        if (!empty) {
            val transformedResults = intermediateResultsTransformer(intermediateConsumers)
            finalConsumer.process(transformedResults)
        }
        intermediateConsumers = intermediateConsumersFactory()
        empty = true
    }

    override fun results() = finalConsumer.results()

    override fun stop() {
        processEndOfSeries()
    }
}

fun<T, V> consumeWithResetting(intermediateConsumersFactory: () -> List<Consumer<T>>,
                               resetTrigger: (intermediateConsumers: List<Consumer<T>>, value: T) -> Boolean,
                               intermediateResultsTransformer: (intermediateConsumers: List<Consumer<T>>) -> V,
                               finalConsumer: Consumer<V>,
                               keepValueThatTriggeredReset: Boolean = false,
                               repeatLastValueInNewSeries: Boolean = false): Consumer<T> =
    Resetter(intermediateConsumersFactory, resetTrigger, intermediateResultsTransformer, finalConsumer,
        keepValueThatTriggeredReset, repeatLastValueInNewSeries)

