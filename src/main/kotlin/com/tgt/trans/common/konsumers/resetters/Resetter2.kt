package com.tgt.trans.common.konsumers.resetters

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder
import com.tgt.trans.common.konsumers.consumers.sometimes

class Resetter2<T, V>(private val intermediateConsumersFactory: () -> List<Consumer<T>>,
                     private val resetTrigger: (intermediateConsumers: List<Consumer<T>>, value: T) -> Boolean,
                     private val intermediateResultsTransformer: (intermediateConsumers: List<Consumer<T>>) -> V,
                     private val finalConsumer: Consumer<V>,
                     private val keepValueThatTriggeredReset: Boolean = false,
                     private val repeatLastValueInNewSeries: Boolean = false): Consumer<T> {
    private var intermediateConsumers: List<Consumer<T>> = intermediateConsumersFactory()
    private var previousValue: T? = null

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
    }

    private fun processEndOfSeries() {
        val transformedResults = intermediateResultsTransformer(intermediateConsumers)
        finalConsumer.process(transformedResults)
        intermediateConsumers = intermediateConsumersFactory()
    }

    override fun results() = finalConsumer.results()

    override fun stop() {
        processEndOfSeries()
    }
}

fun<T, V> consumeWithResetting2(intermediateConsumerFactory: () -> Consumer<T>,
                               resetTrigger: IResetTrigger<T>,
                               intermediateResultsTransformer: (intermediateResults: Any, seriesDescription: Any) -> V,
                               finalConsumer: Consumer<V>,
                               keepValueThatTriggeredReset: Boolean = false,
                               repeatLastValueInNewSeries: Boolean = false): Consumer<T> =
    Resetter(intermediateConsumerFactory, resetTrigger, intermediateResultsTransformer, finalConsumer,
        keepValueThatTriggeredReset, repeatLastValueInNewSeries)


fun<S, T, V> ConsumerBuilder<S, T>.consumeWithResetting2(intermediateConsumerFactory: () -> Consumer<T>,
                                                     resetTrigger: IResetTrigger<T>,
                                                     intermediateResultsTransformer: (intermediateResults: Any, seriesDescription: Any) -> V,
                                                     finalConsumer: Consumer<V>,
                                                     keepValueThatTriggeredReset: Boolean = false,
                                                     repeatLastValueInNewSeries: Boolean = false): Consumer<S> =
    this.build(Resetter(intermediateConsumerFactory, resetTrigger, intermediateResultsTransformer, finalConsumer,
        keepValueThatTriggeredReset, repeatLastValueInNewSeries))
