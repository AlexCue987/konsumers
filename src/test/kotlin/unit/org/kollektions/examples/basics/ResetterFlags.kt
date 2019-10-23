package org.kollektions.examples.basics

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.dispatchers.consumeWithResetting
import kotlin.test.Test
import kotlin.test.assertEquals

class ResetterFlags {
    private val commands = listOf("right", "left", "stop", "up")
    val intermediateResultsTransformer = { intermediateConsumers: List<Consumer<String>> -> intermediateConsumers[0].results() }

    @Test
    fun `no flags set`() {
        val actual = commands.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(asList<String>()) },
                resetTrigger = { _: List<Consumer<String>>, value: String -> value == "stop" },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = asList()
            ))
        println(actual)
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
        println(actual)
        val expected = listOf(listOf("right", "left", "stop"), listOf("up"))
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
        println(actual)
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
        println(actual)
        val expected = listOf(listOf("right", "left", "stop"), listOf("stop", "up"))
        assertEquals(expected, actual[0])
    }

}
