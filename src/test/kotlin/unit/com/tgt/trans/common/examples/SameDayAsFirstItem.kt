package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.WeatherReading
import com.tgt.trans.common.aggregator2.conditions.firstItemCondition
import com.tgt.trans.common.aggregator2.conditions.sameProjectionAsFirst
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.filterOn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class SameDayAsFirstItem {
    val mondayMorningWeather = WeatherReading(LocalDateTime.of(2019, 7, 15, 7, 6), BigDecimal(55))
    val mondayNightWeather = WeatherReading(LocalDateTime.of(2019, 7, 15, 18, 6), BigDecimal(55))
    val tuesdayMorningWeather = WeatherReading(LocalDateTime.of(2019, 7, 16, 7, 6), BigDecimal(55))

    val weatherData = listOf(mondayMorningWeather, mondayNightWeather, tuesdayMorningWeather)

    @Test
    fun `keeps data points for the same day using firstItemCondition`() {
        val sut = firstItemCondition<WeatherReading>
        { firstValue: WeatherReading, currentValue: WeatherReading ->
            firstValue.takenAt.toLocalDate() == currentValue.takenAt.toLocalDate() }
        val actual = weatherData.consume(filterOn(sut).asList())
        assertEquals(listOf(mondayMorningWeather, mondayNightWeather), actual[0])
    }

    @Test
    fun `keeps data points for the same day using sameProjectionAsFirst`() {
        val sut = sameProjectionAsFirst<WeatherReading, LocalDate>
        { currentValue: WeatherReading -> currentValue.takenAt.toLocalDate() }
        val actual = weatherData.consume(filterOn(sut).asList())
        assertEquals(listOf(mondayMorningWeather, mondayNightWeather), actual[0])
    }
}
