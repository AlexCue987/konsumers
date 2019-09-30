package com.tgt.trans.common.aggregator2.resetters

interface ResetTrigger<T> {
    fun process(value: T)
    fun needsResetting(): Boolean
    fun describeSeries(): Any
    val keepValueThatTriggeredReset: Boolean
    fun resetState()
}
