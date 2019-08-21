package com.tgt.trans.common.aggregator2.conditions

class VanillaCondition<T>(private val condition: (a: T) -> Boolean): Condition<T> {
    override fun get(value: T) = condition(value)

    override fun emptyCopy() = this
}
