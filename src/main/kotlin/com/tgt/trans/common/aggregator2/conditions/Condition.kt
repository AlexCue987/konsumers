package com.tgt.trans.common.aggregator2.conditions

interface Condition<T> {
    operator fun get(value: T): Boolean
    fun emptyCopy(): Condition<T>
}
