package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.WeatherByDay
import com.tgt.trans.common.aggregator2.WeatherReading
import com.tgt.trans.common.aggregator2.conditions.FirstItemCondition
import com.tgt.trans.common.aggregator2.conditions.notSameProjectionAsFirst
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.max
import com.tgt.trans.common.aggregator2.consumers.min
import com.tgt.trans.common.aggregator2.decorators.allOf
import com.tgt.trans.common.aggregator2.decorators.groupBy
import com.tgt.trans.common.aggregator2.decorators.mapTo
import com.tgt.trans.common.aggregator2.decorators.peek
import com.tgt.trans.common.aggregator2.resetters.ResetterOnCondition
import com.tgt.trans.common.aggregator2.resetters.withResetting
import java.math.BigDecimal
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
        val dailyAggregates = temperatures.consume(
            groupBy(keyFactory = { it: Temperature -> it.getDate() },
                innerConsumerFactory = { mapTo { it: Temperature -> it.temperature }.allOf(min(), max()) }
            ))
        print(dailyAggregates)
        val expected = mapOf(
            monday to listOf(Optional.of(46), Optional.of(58)),
            tuesday to listOf(Optional.of(44), Optional.of(61))
        )
        assertEquals(expected, dailyAggregates[0])
    }

    fun getSeriesDate(resetter: ResetterOnCondition<Temperature>): LocalDate {
        val condition = resetter.condition as FirstItemCondition
        return condition.getFirstValue()!!.takenAt.toLocalDate()
    }

    fun resetOnDayChange() =
        ResetterOnCondition(keepValueThatTriggeredReset = false,
            condition = notSameProjectionAsFirst { a: Temperature -> a.getDate() },
            seriesDescriptor = { it -> getSeriesDate(it) } )

    fun mapResultsToDailyWeather(intermediateResults: Any, day: Any): DailyWeather {
        val consumers = intermediateResults as List<Any>
        val minConsumer = consumers[0] as Optional<Int>
        val maxConsumer = consumers[1] as Optional<Int>
        return DailyWeather(day as LocalDate, minConsumer.get(), maxConsumer.get())
    }

    @Test
    fun `daily aggregates available as soon as day ends`() {
        val intermediateResultsTransformer = { intermediateResults: Any, day: Any -> mapResultsToDailyWeather(intermediateResults, day) }
        val finalConsumer = peek<DailyWeather> { println("Consuming $it") }.asList()
        val dailyAggregates = temperatures.consume(
            peek<Temperature> { println("Consuming $it") }
                .mapTo { it: Temperature -> it.temperature }
                .allOf(min(), max())
                .withResetting(resetTrigger = resetOnDayChange(),
                    intermediateResultsTransformer = intermediateResultsTransformer,
                    finalConsumer = finalConsumer))
        print(dailyAggregates)
        val expected = listOf(DailyWeather(monday, 46, 58),
            DailyWeather(tuesday, 44, 61))
        assertEquals(expected, dailyAggregates[0])
    }
}
