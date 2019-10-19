package com.tgt.trans.common.examples.extending

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder
import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.transformations.filterOn
import com.tgt.trans.common.testutils.FakeStopTester
import kotlin.test.Test
import kotlin.test.assertTrue

class NewTransformation {

    @Test
    fun `prints items as first transformation`() {
        (0..2).asSequence().consume(print<Int>().asList())
    }

    /* Output:
Processing item 0
Processing item 1
Processing item 2
    */

    @Test
    fun `prints items as second transformation`() {
        (0..2).asSequence().consume(filterOn<Int> { it>0 }.print().asList())
    }

    /* Output:
Processing item 1
Processing item 2
    */

    @Test
    fun `passes stop() call downstream`() {
        val innerConsumer = FakeStopTester<Int>()
        (0..2).asSequence().consume(filterOn<Int> { it>0 }.print().allOf(innerConsumer))
        assertTrue(innerConsumer.results())
    }

    private class Printer<T>(private val innerConsumer: Consumer<T>): Consumer<T> {
        override fun process(value: T) {
            print("Processing item $value\n")
            innerConsumer.process(value)
        }

        override fun results() = innerConsumer.results()

        override fun stop() { innerConsumer.stop() }
    }

    private class PrinterBuilder<T>: ConsumerBuilder<T, T> {
        override fun build(innerConsumer: Consumer<T>): Consumer<T> = Printer(innerConsumer)
    }

    private fun<T> print() = PrinterBuilder<T>()

    private class ChainedPrinterBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>): ConsumerBuilder<T, V> {
        override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Printer(innerConsumer))
    }

    private fun<T, V> ConsumerBuilder<T, V>.print(): ConsumerBuilder<T, V> = ChainedPrinterBuilder(this)
}
