package com.tgt.trans.common.konsumers.consumers

interface ConsumerBuilder<T, V> {
    fun build(innerConsumer: Consumer<V>): Consumer<T>
}

interface Consumer<T> {
    fun process(value: T)
    fun results(): Any
    fun stop() {}
}

fun<T> Iterable<T>.consume(vararg consumers: Consumer<T>): List<Any> {
    val consumersList = consumers.toList()
    val iterator = iterator()
    return consume(iterator, consumersList)
}

fun<T> Sequence<T>.consume(vararg consumers: Consumer<T>): List<Any> {
    val consumersList = consumers.toList()
    val iterator = iterator()
    return consume(iterator, consumersList)
}

private fun <T> consume(iterator: Iterator<T>, consumersList: List<Consumer<T>>): List<Any> {
    while (iterator.hasNext()) {
        val value = iterator.next()
        consumersList.forEach { it.process(value) }
    }
    consumersList.forEach { it.stop() }
    return consumersList.map { it.results() }
}
