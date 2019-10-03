package com.tgt.trans.common.aggregator2.resetters

import com.tgt.trans.common.aggregator2.consumers.Consumer

class ResetterOnCondition2<T>(override val keepValueThatTriggeredReset: Boolean,
                              private val stateFactory: () -> Consumer<T>,
                              private val stateType: StateType,
                              private val condition: (state: Consumer<T>, value: T) -> Boolean,
                              private val seriesDescriptor: (state: Consumer<T>) -> Any): ResetTrigger<T> {
    override fun describeSeries()= seriesDescriptor(state)

    var resettingDetected = false
    private var state = stateFactory()

    override fun process(value: T) {
        if (stateType == StateType.Before) {
            state.process(value)
        }
        resettingDetected = condition(state, value)
        if (stateType == StateType.After) {
            state.process(value)
        }
    }

    override fun needsResetting() = resettingDetected

    override fun resetState() {
        resettingDetected = false
        state = stateFactory()
    }

    enum class StateType{ Before, After }
}
