package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.resetters.ResetTrigger
import com.tgt.trans.common.konsumers.resetters.consumeWithResetting
import com.tgt.trans.common.konsumers.transformations.mapTo
import com.tgt.trans.common.konsumers.transformations.peek
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class WarmingCooling {
    private val temperatures = listOf(
        Temperature(LocalDateTime.of(2019, 10, 9, 8, 7), 44),
        Temperature(LocalDateTime.of(2019, 10, 9, 12, 7), 49),
        Temperature(LocalDateTime.of(2019, 10, 9, 15, 7), 56),
        Temperature(LocalDateTime.of(2019, 10, 9, 18, 7), 48),
        Temperature(LocalDateTime.of(2019, 10, 10, 5, 7), 42),
        Temperature(LocalDateTime.of(2019, 10, 10, 11, 7), 49)
        )

    @Test
    fun `split temperatures into increasing and decreasing subseries`() {
        val intermediateResultsTransformer = { intermediateResults: Any, resetterState: Any -> getSubseriesStats(intermediateResults, resetterState) }

        val actual = temperatures.consume(consumeWithResetting(intermediateConsumerFactory = { getStatsConsumer() },
            resetTrigger = resetOnDirectionChange(),
            intermediateResultsTransformer = intermediateResultsTransformer,
            finalConsumer = peek<SubseriesStats> { println("Consuming $it") }.asList()))

        print(actual)
    }

    private fun getStatsConsumer() = peek<Temperature> { println("Consuming $it") }.
        allOf(
            mapTo<Temperature, LocalDateTime> { it.takenAt }.allOf(min(), max()),
            mapTo<Temperature, Int> { it.temperature }.allOf(min(), max())
        )

    private fun resetOnDirectionChange() =
        ResetTrigger(
            stateFactory = { LastN<Temperature>(3) },
            stateType = ResetTrigger.StateType.Before,
            condition = { state: Consumer<Temperature>, value: Temperature -> changeInAnotherDirection(state as LastN, value)},
            seriesDescriptor = { 42} )

    private fun changeInAnotherDirection(state: LastN<Temperature>, newValue: Temperature) = when(state.results().size) {
        1, 2 -> false
        else -> {
            val penultimateValue = state.results()[0].temperature
            val previousValue = state.results()[1].temperature
            penultimateValue.compareTo(previousValue) == -previousValue.compareTo(newValue.temperature)
        }
    }

    private fun getSubseriesStats(intermediateResults: Any, resetterState: Any): SubseriesStats {
        val timesForSeries = ((intermediateResults as List<Any>)[0] as List<Any>)
        val startedAt = timesForSeries[0] as Optional<LocalDateTime>
        val endedAt = timesForSeries[1] as Optional<LocalDateTime>

        val temperaturesForSeries = ((intermediateResults as List<Any>)[1] as List<Any>)
        val temperatureAtStart = temperaturesForSeries[0] as Optional<Int>
        val temperatureAtEnd = temperaturesForSeries[1] as Optional<Int>
        return SubseriesStats(startedAt.get(), endedAt.get(), temperatureAtStart.get(), temperatureAtEnd.get())
    }

    private data class Temperature(val takenAt: LocalDateTime, val temperature: Int)

    private data class SubseriesStats(val startedAt: LocalDateTime,
                                 val endedAt: LocalDateTime,
                                 val temperatureAtStart: Int,
                                 val temperatureAtEnd: Int)
}
