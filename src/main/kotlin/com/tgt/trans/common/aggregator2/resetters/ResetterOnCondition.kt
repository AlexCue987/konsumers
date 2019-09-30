package com.tgt.trans.common.aggregator2.resetters

import com.tgt.trans.common.aggregator2.conditions.Condition
import com.tgt.trans.common.aggregator2.conditions.VanillaCondition
import com.tgt.trans.common.aggregator2.consumers.Consumer

class ResetterOnCondition<T>(override val keepValueThatTriggeredReset: Boolean,
                             var condition: Condition<T>,
                             private val seriesDescriptor: (a: ResetterOnCondition<T>) -> Any = { it -> it.toString() }): ResetTrigger<T> {
    var resettingDetected = false

    override fun process(value: T) {
        resettingDetected = condition[value]
    }

    override fun needsResetting() = resettingDetected

    override fun describeSeries() = seriesDescriptor(this)

    override fun resetState() {
        resettingDetected = false
        condition = condition.emptyCopy()
    }
}

//private fun<T> defaultDescriptor(a: ResetterOnCondition<T>) = "ResetterOnCondition"

//fun<T, V> Consumer<T>.resetWhen(condition: Condition<T>,
//                                intermediateResultsTransformer: (a: Any, b: Any) -> V,
//                                finalConsumer: Consumer<V>,
//                                descriptor: (a: ResetterOnCondition<T>) -> String = { it -> defaultDescriptor(it) } ): Consumer<T> =
//    Resetter(this,
//        ResetterOnCondition(keepValueThatTriggeredReset = false, condition = condition, seriesDescriptor = descriptor),
//        intermediateResultsTransformer,
//        finalConsumer)
//
//fun<T, V> Consumer<T>.resetAfter(condition: Condition<T>,
//                                intermediateResultsTransformer: (a: Any, b: Any) -> V,
//                                finalConsumer: Consumer<V>,
//                                descriptor: (a: ResetterOnCondition<T>) -> String = { it -> defaultDescriptor(it) }): Consumer<T> =
//    Resetter(this,
//        ResetterOnCondition(keepValueThatTriggeredReset = true, condition = condition, seriesDescriptor = descriptor),
//        intermediateResultsTransformer,
//        finalConsumer)
//
//fun<T, V> Consumer<T>.resetWhen(condition: (value: T) -> Boolean,
//                                intermediateResultsTransformer: (a: Any, b: Any) -> V,
//                                finalConsumer: Consumer<V>,
//                                descriptor: (a: ResetterOnCondition<T>) -> String = { it -> defaultDescriptor(it) }): Consumer<T> =
//    Resetter(this,
//        ResetterOnCondition(keepValueThatTriggeredReset = false, condition = VanillaCondition(condition), seriesDescriptor = descriptor),
//        intermediateResultsTransformer,
//        finalConsumer)
//
//fun<T, V> Consumer<T>.resetAfter(condition: (value: T) -> Boolean,
//                                 intermediateResultsTransformer: (a: Any, b: Any) -> V,
//                                 finalConsumer: Consumer<V>,
//                                 descriptor: (a: ResetterOnCondition<T>) -> String = { it -> defaultDescriptor(it) }): Consumer<T> =
//    Resetter(this,
//        ResetterOnCondition(keepValueThatTriggeredReset = true, condition = VanillaCondition(condition), seriesDescriptor = descriptor),
//        intermediateResultsTransformer,
//        finalConsumer)
