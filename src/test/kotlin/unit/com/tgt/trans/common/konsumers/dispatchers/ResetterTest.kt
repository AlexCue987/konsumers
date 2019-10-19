package com.tgt.trans.common.konsumers.dispatchers

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.dispatchers.consumeWithResetting
import com.tgt.trans.common.testutils.FakeStopTester
import kotlin.test.Test
import kotlin.test.assertEquals

class ResetterTest {
    private val commands = listOf("right", "left", "stop", "up")
    val intermediateResultsTransformer = { intermediateConsumers: List<Consumer<String>> -> intermediateConsumers[0].results() }

    @Test
    fun `handles empty sequence`() {
        val actual = listOf<String>().consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList()
            ))
        val expected = listOf<List<List<String>>>()
        assertEquals(expected, actual[0])
    }

    @Test
    fun `no flags set`() {
        val actual = commands.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList()
            ))
        val expected = listOf(listOf("right", "left"), listOf("stop", "up"))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `keepValueThatTriggeredReset is true`() {
        val actual = commands.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList(),
                keepValueThatTriggeredReset = true
            ))
        val expected = listOf(listOf("right", "left", "stop"), listOf("up"))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `keepValueThatTriggeredReset is true and reset on last item`() {
        val actual = listOf("right", "left", "stop").consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList(),
                keepValueThatTriggeredReset = true
            ))
        val expected = listOf(listOf("right", "left", "stop"))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `repeatLastValueInNewSeries is true`() {
        val actual = commands.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList(),
                repeatLastValueInNewSeries = true
            ))
        val expected = listOf(listOf("right", "left"), listOf("left", "stop", "up"))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `keepValueThatTriggeredReset and repeatLastValueInNewSeries are both true`() {
        val actual = commands.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList(),
                repeatLastValueInNewSeries = true,
                keepValueThatTriggeredReset = true
            ))
        val expected = listOf(listOf("right", "left", "stop"), listOf("stop", "up"))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `keepValueThatTriggeredReset and repeatLastValueInNewSeries are both true and reset on last item`() {
        val actual = listOf("right", "left", "stop").consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList(),
                repeatLastValueInNewSeries = true,
                keepValueThatTriggeredReset = true
            ))
        val expected = listOf(listOf("right", "left", "stop"), listOf("stop"))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `stops consumers on resetting`() {
        val actual = commands.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(FakeStopTester<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList(),
                repeatLastValueInNewSeries = true,
                keepValueThatTriggeredReset = true
            ))
        val expected = listOf(true, true)
        assertEquals(expected, actual[0])
    }
}
