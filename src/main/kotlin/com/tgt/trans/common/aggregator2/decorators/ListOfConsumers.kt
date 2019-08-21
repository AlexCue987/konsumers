package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class ListOfConsumers<T>(vararg consumersArgs: Consumer<T>): Consumer<T> {
    private val consumers = consumersArgs.toList()
    override fun emptyCopy(): Consumer<T> = ListOfConsumers(*consumers.map { it.emptyCopy() }.toTypedArray())

    override fun process(value: T) {
        consumers.forEach { it.process(value) }
    }

    override fun results() = consumers.map { it.results() }


    override fun isEmpty() = consumers.all {it.isEmpty()}

    override fun stop() = consumers.forEach { it.stop() }
}

fun<T> allOf2(vararg aggregatorsArgs: Consumer<T>) = ListOfConsumers(*aggregatorsArgs)

fun<S, T> ConsumerBuilder<S, T>.allOf2(vararg aggregatorsArgs: Consumer<T>) =
        this.build(ListOfConsumers(*aggregatorsArgs))
