package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.transformations.filterOn
import com.tgt.trans.common.konsumers.transformations.mapTo
import com.tgt.trans.common.examples.DailyWeather
import com.tgt.trans.common.examples.dailyWeather
import java.math.BigDecimal
import kotlin.test.Test

class ReusingFilteringAndMapping {
    @Test
    fun `reuses filtering`() {
        val verySlowFilter = filterOn<DailyWeather> { it -> it.rainAmount > BigDecimal.ZERO }

        val lowestLowTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val rainyDaysCount = count<DailyWeather>()

        val allResults = dailyWeather.consume(
            verySlowFilter.allOf(lowestLowTemperature, rainyDaysCount))

        println(lowestLowTemperature.results())
        println(rainyDaysCount.results())
        println(allResults)
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
