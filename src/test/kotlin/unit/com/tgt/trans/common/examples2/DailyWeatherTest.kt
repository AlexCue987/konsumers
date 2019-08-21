package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.WeatherByDay
import com.tgt.trans.common.aggregator2.WeatherReading
import com.tgt.trans.common.aggregator2.conditions.FirstItemCondition
import com.tgt.trans.common.aggregator2.conditions.notSameProjectionAsFirst
import com.tgt.trans.common.aggregator2.conditions.sameProjectionAsFirst
import com.tgt.trans.common.aggregator2.consumers.*
import com.tgt.trans.common.aggregator2.decorators.allOf2
import com.tgt.trans.common.aggregator2.decorators.groupBy
import com.tgt.trans.common.aggregator2.decorators.mapTo
import com.tgt.trans.common.aggregator2.decorators.peek
import com.tgt.trans.common.aggregator2.resetters.ResetterOnCondition
import com.tgt.trans.common.aggregator2.resetters.withResetting
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DailyWeatherTest {
    val monday = LocalDate.of(2019, 7, 8)
    val mondayMorning = WeatherReading(LocalDateTime.of(2019, 7, 8, 0, 1, 2), BigDecimal.valueOf(62L))
    val mondayNight = WeatherReading(LocalDateTime.of(2019, 7, 8, 16, 15, 14), BigDecimal.valueOf(84L))
    val tuesday = LocalDate.of(2019, 7, 9)
    val tuesdayMorning = WeatherReading(LocalDateTime.of(2019, 7, 9, 1, 2, 3), BigDecimal.valueOf(55))
    val tuesdayNight = WeatherReading(LocalDateTime.of(2019, 7, 9, 16, 15, 14), BigDecimal.valueOf(88))

    val allWeatherReadings = listOf(mondayMorning, mondayNight, tuesdayMorning, tuesdayNight)
    val expectedDailyWeather = listOf(
        WeatherByDay(monday, BigDecimal.valueOf(62L), BigDecimal.valueOf(84L)),
        WeatherByDay(tuesday, BigDecimal.valueOf(55L), BigDecimal.valueOf(88L))
    )

    @Test
    fun `Monday aggregates available only after consuming all data`() {
        val actual = allWeatherReadings.consume(
            groupBy<WeatherReading, LocalDate> { it.takenAt.toLocalDate() }
                .mapTo { it.degrees }
                .allOf2(min2(), max2()))
        print(actual)
    }

    fun getSeriesDate(resetter: ResetterOnCondition<WeatherReading>): LocalDate {
        val condition = resetter.condition as FirstItemCondition
        return condition.getFirstValue()!!.takenAt.toLocalDate()
    }

    fun resetOnDayChange() =
        ResetterOnCondition(keepValueThatTriggeredReset = false,
            condition = notSameProjectionAsFirst { a: WeatherReading -> a.takenAt.toLocalDate() },
            seriesDescriptor = { it -> getSeriesDate(it) } )

    fun mapResultsToWeatherByDay(intermediateResults: Any, day: Any): WeatherByDay {
        val consumers = intermediateResults as List<Any>
        val minConsumer = consumers[0] as Optional<BigDecimal>
        val maxConsumer = consumers[1] as Optional<BigDecimal>
        return WeatherByDay(day as LocalDate, minConsumer.get(), maxConsumer.get())
    }

    @Test
    fun `Monday aggregates available as soon as we know Monday is over`() {
        val intermediateResultsTransformer =
            { intermediateResults: Any, day: Any -> mapResultsToWeatherByDay(intermediateResults, day) }
        val finalConsumer = peek<WeatherByDay> { print("Consuming daily weather $it\n") }.asList()
        val actual = allWeatherReadings.consume(
                mapTo<WeatherReading, BigDecimal> { it.degrees }
                    .peek { print("Consuming weather reading $it\n") }
                .allOf2(min2(), max2())
                    .withResetting(resetTrigger = resetOnDayChange(),
                        intermediateResultsTransformer = intermediateResultsTransformer,
                        finalConsumer = finalConsumer)
        )
        assertEquals(expectedDailyWeather, actual[0])
    }
}

