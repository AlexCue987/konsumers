package org.kollektions.examples.basics

import org.kollektions.consumers.*
import org.kollektions.dispatchers.allOf
import org.kollektions.dispatchers.groupBy
import org.kollektions.transformations.mapTo
import org.kollektions.transformations.peek
import org.kollektions.dispatchers.consumeWithResetting
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HighAndLowTemperature {
    val monday = LocalDate.of(2019, 9, 23)
    val tuesday = LocalDate.of(2019, 9, 24)
    val morning = LocalTime.of(7, 15)
    val night = LocalTime.of(17, 20)

    private val temperatures = listOf(
        Temperature(monday.atTime(morning), 46),
        Temperature(monday.atTime(night), 58),
        Temperature(tuesday.atTime(morning), 44),
        Temperature(tuesday.atTime(night), 61)
    )

    data class Temperature(val takenAt: LocalDateTime, val temperature: Int) {
        fun getDate() = takenAt.toLocalDate()
    }

    data class DailyWeather(val date: LocalDate, val low: Int, val high: Int)

    @Test
    fun `aggregate temperatures the usual way`() {
        val rawDailyAggregates = temperatures.consume(
            groupBy(keyFactory = { it: Temperature -> it.getDate() },
                innerConsumerFactory = { mapTo { it: Temperature -> it.temperature }.allOf(min(), max()) }
            ))
        print(rawDailyAggregates)
        val finalDailyAggregates = (rawDailyAggregates[0] as Map<LocalDate, List<Optional<Int>>>)
            .entries
            .map { DailyWeather(it.key, it.value[0].get(), it.value[1].get()) }
        val expected = listOf(DailyWeather(monday, 46, 58),
            DailyWeather(tuesday, 44, 61))
        assertEquals(expected, finalDailyAggregates)
    }

    fun mapResultsToDailyWeather(intermediateConsumers: List<Consumer<Temperature>>): DailyWeather {
        val results = intermediateConsumers.map { it.results() }
        val highAndLow = (results[0] as List<Any>)
        val lowTemperature = highAndLow[0] as Optional<Int>
        val highTemperature = highAndLow[1] as Optional<Int>
        val day = (results[1] as Optional<LocalDate>).get()
        return DailyWeather(day, lowTemperature.get(), highTemperature.get())
    }

    @Test
    fun `daily aggregates available as soon as day ends`() {
        val intermediateConsumer = {
            peek<Temperature> { println("Consuming $it") }
            .mapTo { it: Temperature -> it.temperature }
            .allOf(min(), max()) }

        val stateToStoreDay = { mapTo<Temperature, LocalDate> {it.getDate()}.first() }

        val intermediateResultsTransformer =
            { intermediateConsumers: List<Consumer<Temperature>> -> mapResultsToDailyWeather(intermediateConsumers) }

        val dailyAggregates = temperatures.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(intermediateConsumer(), stateToStoreDay()) },
                resetTrigger = dateChange(),
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = peek<DailyWeather> { println("Consuming $it") }.asList()))

        print(dailyAggregates)

        val expected = listOf(DailyWeather(monday, 46, 58),
            DailyWeather(tuesday, 44, 61))

        assertEquals(expected, dailyAggregates[0])
    }

    private fun dateChange() = { intermediateConsumers: List<Consumer<Temperature>>, value: Temperature ->
        val optionalDay = intermediateConsumers[1].results() as Optional<LocalDate>
         optionalDay.isPresent && optionalDay.get() != value.getDate()
    }
}
