package com.tgt.trans.common.konsumers.consumers

interface ConsumerBuilder<T, V> {
    fun build(innerConsumer: Consumer<V>): Consumer<T>
}

interface Consumer<T> {
    fun process(value: T)
    fun results(): Any
    fun stop()
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
    return consume2(iterator, consumersList) { consumers: List<Consumer<T>> -> defaultResultsMapper(consumers)}
}

fun<T, V> Sequence<T>.consume(resultsMapper: (consumersList: List<Consumer<T>>) -> V,
                              vararg consumers: Consumer<T>): V {
    val consumersList = consumers.toList()
    val iterator = iterator()
    return consume2(iterator, consumersList, resultsMapper)
}

fun<T, V> Iterable<T>.consume(resultsMapper: (consumersList: List<Consumer<T>>) -> V,
                              vararg consumers: Consumer<T>): V {
    val consumersList = consumers.toList()
    val iterator = iterator()
    return consume2(iterator, consumersList, resultsMapper)
}

private fun <T, V> consume2(iterator: Iterator<T>, consumersList: List<Consumer<T>>,
                            resultsMapper: (consumersList: List<Consumer<T>>) -> V): V {
    while (iterator.hasNext()) {
        val value = iterator.next()
        consumersList.forEach { it.process(value) }
    }
    consumersList.forEach { it.stop() }
    return resultsMapper(consumersList)
}

private fun<T> defaultResultsMapper(consumersList: List<Consumer<T>>) = consumersList.map { it.results() }
