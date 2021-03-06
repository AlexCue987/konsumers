package org.kollektions.consumers

class ConditionOnRatio<T>(private val conditionOnItem: (a: T) -> Boolean,
                          val conditionOnRatio: (conditionMet: Long, outOf: Long) -> Boolean)
    : Consumer<T> {
    val consumer = RatioCounter(conditionOnItem)

    override  fun process(value: T) {
        consumer.process(value)
    }

    override  fun results() = conditionOnRatio(consumer.conditionMet, consumer.outOf)

    override  fun stop() {}
}


fun<T> never(condition: (a: T) -> Boolean) = ConditionOnRatio(condition) { conditionMet: Long, _: Long -> conditionMet == 0L }

fun<T, V> ConsumerBuilder<T, V>.never(condition: (a: V) -> Boolean) =
    this.build(ConditionOnRatio(condition) { conditionMet: Long, _: Long -> conditionMet == 0L })


fun<T> always(condition: (a: T) -> Boolean) = ConditionOnRatio(condition) { conditionMet: Long, outOf: Long -> conditionMet == outOf }

fun<T, V> ConsumerBuilder<T, V>.always(condition: (a: V) -> Boolean) =
    this.build(ConditionOnRatio(condition) { conditionMet: Long, outOf: Long -> conditionMet == outOf })


fun<T> sometimes(condition: (a: T) -> Boolean) = ConditionOnRatio(condition) { conditionMet: Long, _: Long -> conditionMet > 0L }

fun<T, V> ConsumerBuilder<T, V>.sometimes(condition: (a: V) -> Boolean) =
    this.build(ConditionOnRatio(condition) { conditionMet: Long, _: Long -> conditionMet > 0L })
