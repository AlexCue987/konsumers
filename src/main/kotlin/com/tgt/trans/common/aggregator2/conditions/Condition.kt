package com.tgt.trans.common.aggregator2.conditions

interface Condition<T> {
    operator fun get(value: T): Boolean
    fun emptyCopy(): Condition<T>
}

interface State<T, V> {
    fun process(value: T)
    fun stateValue(): V
}
