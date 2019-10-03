package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.*
import com.tgt.trans.common.aggregator2.decorators.allOf
import com.tgt.trans.common.aggregator2.decorators.groupBy
import com.tgt.trans.common.aggregator2.decorators.mapTo
import com.tgt.trans.common.aggregator2.decorators.peek
import com.tgt.trans.common.aggregator2.resetters.ResetTrigger
import com.tgt.trans.common.aggregator2.resetters.consumeWithResetting
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

    fun resetOnDayChange() =
        ResetTrigger(keepValueThatTriggeredReset = false,
            stateFactory = { FirstN<Temperature>(1) },
            stateType = ResetTrigger.StateType.Before,
            condition = { state: Consumer<Temperature>, value: Temperature -> (state as FirstN).results()[0].getDate() != value.getDate()},
            seriesDescriptor = { state: Consumer<Temperature> -> (state as FirstN).results()[0].getDate()} )

    fun mapResultsToDailyWeather(intermediateResults: Any, day: Any): DailyWeather {
        val consumers = intermediateResults as List<Any>
        val lowTemperature = consumers[0] as Optional<Int>
        val highTemperature = consumers[1] as Optional<Int>
        return DailyWeather(day as LocalDate, lowTemperature.get(), highTemperature.get())
    }

    @Test
    fun `daily aggregates available as soon as day ends`() {
        val intermediateResultsTransformer = { intermediateResults: Any, day: Any -> mapResultsToDailyWeather(intermediateResults, day) }
        val intermediateConsumer = peek<Temperature> { println("Consuming $it") }
            .mapTo { it: Temperature -> it.temperature }
            .allOf(min(), max())
        val dailyAggregates = temperatures.consume(
            consumeWithResetting(
                intermediateConsumerFactory = { intermediateConsumer },
                resetTrigger = resetOnDayChange(),
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = peek<DailyWeather> { println("Consuming $it") }.asList()))
        print(dailyAggregates)
        val expected = listOf(DailyWeather(monday, 46, 58),
            DailyWeather(tuesday, 44, 61))
        assertEquals(expected, dailyAggregates[0])
    }
}
