package org.kollektions.dispatchers

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.transformations.filterOn
import org.kollektions.testutils.FakeStopTester
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupDispatcherTest {
    private val sut =
        filterOn<TestThing> { it.color != "Red" }
            .groupBy(keyFactory = {it: TestThing -> it.color},
                innerConsumerFactory = { asList() })

    @Test
    fun `handles empty`() {
        val actual = listOf<TestThing>().consume(sut)
        val expected = mapOf<String, TestThing>()
        assertEquals(expected, actual[0])
    }

    @Test
    fun `handles non-empty`() {
        val actual = things.consume(sut)
        val expected = mapOf("Blue" to listOf(blueSquare, blueCircle),
            "Amber" to listOf(amberSquare))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `passes stop downstream`() {
        val sut =
            filterOn<TestThing> { it.color != "Red" }
                .groupBy(keyFactory = {it: TestThing -> it.color},
                    innerConsumerFactory = { FakeStopTester() })
        sut.process(blueCircle)
        sut.process(amberSquare)
        sut.stop()
        val actual = sut.results()
        val expected = mapOf("Blue" to true,
            "Amber" to true)
        assertEquals(expected, actual)
    }

    private data class TestThing(val color: String, val shape: String)

    private val blueSquare = TestThing("Blue", "Square")
    private val blueCircle = TestThing("Blue", "Circle")
    private val amberSquare = TestThing("Amber", "Square")
    private val things = listOf(
        blueSquare,
        blueCircle,
        amberSquare,
        TestThing("Red", "Dot")
    )
}
