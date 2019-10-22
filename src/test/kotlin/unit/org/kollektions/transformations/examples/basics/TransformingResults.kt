package org.kollektions.transformations.examples.basics

import org.kollektions.consumers.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformingResults {

    @Test
    fun `by default consume returns a List of Any`() {
        val actual = (0..10).asSequence().consume(min(), max(), count())
        assertEquals(listOf(
                Optional.of(0),
                Optional.of(10),
                11L),
            actual)
    }

    @Test
    fun `transform results`() {
        val actual = (0..10).asSequence().consume(
            {consumersList: List<Consumer<Int>> -> resultsMapper(consumersList) },
            min(), max(), count())
        assertEquals(BasicStats(Optional.of(0), Optional.of(10), 11L), actual)
    }

    private data class BasicStats(val min: Optional<Int>, val max: Optional<Int>, val count: Long)

    private fun resultsMapper(consumers: List<Consumer<Int>>) =
        BasicStats(
            min = (consumers[0] as Min<Int>).results(),
            max = (consumers[1] as Max<Int>).results(),
            count= (consumers[2] as Counter<Int>).results()
            )
}
