package com.tgt.trans.common.konsumers.resetters

interface IResetTrigger<T> {
    fun process(value: T)
    fun needsResetting(): Boolean
    fun describeSeries(): Any
    fun resetState()
}
