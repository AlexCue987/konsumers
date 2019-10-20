package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.dispatchers.consumeWithResetting
import com.tgt.trans.common.konsumers.transformations.mapTo
import com.tgt.trans.common.konsumers.transformations.peek
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class WarmingCooling {
    val wednesdayMorning = LocalDateTime.of(2019, 10, 9, 8, 7)
    val wednesdayNoon = LocalDateTime.of(2019, 10, 9, 12, 7)
    val warmestTime = LocalDateTime.of(2019, 10, 9, 15, 7)
    val midnight = LocalDateTime.of(2019, 10, 9, 23, 59)
    val coldestTime = LocalDateTime.of(2019, 10, 10, 5, 7)
    val thursdayMorning = LocalDateTime.of(2019, 10, 10, 11, 7)

    private val temperatures = listOf(
        Temperature(wednesdayMorning, 44),
        Temperature(wednesdayNoon, 49),
        Temperature(warmestTime, 56),
        Temperature(midnight, 48),
        Temperature(coldestTime, 42),
        Temperature(thursdayMorning, 49)
        )

    @Test
    fun `split temperatures into increasing and decreasing subseries`() {
        val intermediateResultsTransformer = { intermediateConsumers: List<Consumer<Temperature>> -> getSubseriesStats(intermediateConsumers) }

        val actual = temperatures.consumeByOne(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(getStatsConsumer(), LastN<Temperature>(3)) },
                resetTrigger = { intermediateConsumers: List<Consumer<Temperature>>, value: Temperature ->
                    changeInAnotherDirection(intermediateConsumers, value)
                },
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = peek<SubseriesStats> { println("Consuming $it") }.asList(),
                repeatLastValueInNewSeries = true))

        val expected = listOf(
            SubseriesStats(wednesdayMorning, warmestTime, 44, 56),
            SubseriesStats(warmestTime, coldestTime, 42, 56),
            SubseriesStats(coldestTime, thursdayMorning, 42, 49)
        )

        assertEquals(expected, actual)
    }

    private fun getStatsConsumer() = peek<Temperature> { println("Consuming $it") }.
        allOf(
            mapTo<Temperature, LocalDateTime> { it.takenAt }.allOf(min(), max()),
            mapTo<Temperature, Int> { it.temperature }.allOf(min(), max())
        )

    private fun changeInAnotherDirection(intermediateConsumers: List<Consumer<Temperature>>, newValue: Temperature): Boolean {
        val state: LastN<Temperature> = intermediateConsumers[1] as LastN<Temperature>
        val lastThreeDataPoints = state.results()
        return when (lastThreeDataPoints.size) {
            0, 1, 2 -> false
            else -> {
                val penultimateValue = lastThreeDataPoints[0].temperature
                val previousValue = lastThreeDataPoints[1].temperature
                penultimateValue.compareTo(previousValue) == -previousValue.compareTo(newValue.temperature)
            }
        }
    }

    private fun getSubseriesStats(intermediateConsumers: List<Consumer<Temperature>>): SubseriesStats {
        val timesForSeries = ((intermediateConsumers[0].results() as List<Any>)[0] as List<Any>)
        val startedAt = timesForSeries[0] as Optional<LocalDateTime>
        val endedAt = timesForSeries[1] as Optional<LocalDateTime>

        val temperaturesForSeries = ((intermediateConsumers[0].results() as List<Any>)[1] as List<Any>)
        val temperatureAtStart = temperaturesForSeries[0] as Optional<Int>
        val temperatureAtEnd = temperaturesForSeries[1] as Optional<Int>
        return SubseriesStats(startedAt.get(), endedAt.get(), temperatureAtStart.get(), temperatureAtEnd.get())
    }

    private data class Temperature(val takenAt: LocalDateTime, val temperature: Int)

    private data class SubseriesStats(val startedAt: LocalDateTime,
                                      val endedAt: LocalDateTime,
                                      val lowTemperature: Int,
                                      val highTemperature: Int)
}
