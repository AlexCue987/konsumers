package org.kollektions.examples.basics

import org.kollektions.consumers.*
import org.kollektions.dispatchers.allOf
import org.kollektions.transformations.filterOn
import org.kollektions.transformations.mapTo
import org.kollektions.examples.DailyWeather
import org.kollektions.examples.dailyWeather
import java.math.BigDecimal
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ReusingFilteringAndMapping {
    @Test
    fun `reuses filtering`() {
        val verySlowFilter = filterOn<DailyWeather> { it -> it.rainAmount > BigDecimal.ZERO }

        val lowestLowTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val rainyDaysCount = count<DailyWeather>()

        val allResults = dailyWeather.consumeByOne(
            verySlowFilter.allOf(lowestLowTemperature, rainyDaysCount))

        println(lowestLowTemperature.results())
        println(rainyDaysCount.results())
        println(allResults)

        assertEquals(Optional.of(20), lowestLowTemperature.results())
        assertEquals(6, rainyDaysCount.results())
    }

    @Test
    fun `reuses mapping`() {
        val minTemperature = min<Int>()

        val maxTemperature = max<Int>()

        val dayCount = count<DailyWeather>()

        val verySlowMapping = mapTo<DailyWeather, Int> { it -> it.low }

        val allResults = dailyWeather.consume(
            verySlowMapping.allOf(minTemperature, maxTemperature),
            dayCount)

        println(minTemperature.results())
        println(maxTemperature.results())
        println(dayCount.results())
        println(allResults)
    }

    @Test
    fun `nested calls`() {
        val minTemperature = min<Int>()

        val maxTemperature = max<Int>()

        val rainyDaysCount = count<DailyWeather>()

        val allDaysCount = count<DailyWeather>()

        val verySlowFilter = filterOn<DailyWeather> { it -> it.rainAmount > BigDecimal.ZERO }
        val verySlowMapping = mapTo<DailyWeather, Int> { it -> it.low }

        val allResults = dailyWeather.consume(
            verySlowFilter.allOf(
                verySlowMapping.allOf(minTemperature, maxTemperature),
                rainyDaysCount),
            allDaysCount)

        println(minTemperature.results())
        println(maxTemperature.results())
        println(rainyDaysCount.results())
        println(allDaysCount.results())
        println(allResults)
    }
}
