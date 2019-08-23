package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.*
import com.tgt.trans.common.aggregator2.decorators.allOf
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.mapTo
import com.tgt.trans.common.examples.DailyWeather
import com.tgt.trans.common.examples.dailyWeather
import java.math.BigDecimal
import kotlin.test.Test


class BranchingAfterTransformation {
    @Test
    fun `reuses filtering`() {
        val lowestLowTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val lowestHighTemperature = mapTo<DailyWeather, Int> { it -> it.high }
            .min()

        val rainyDaysCount = counter<DailyWeather>()

        val dayCount = counter<DailyWeather>()

        val verySlowFilter = filterOn<DailyWeather> { it -> it.rainAmount > BigDecimal.ZERO }

        val allResults = dailyWeather.consume(
            verySlowFilter.allOf(lowestLowTemperature, lowestHighTemperature, rainyDaysCount),
            dayCount)

        println(lowestHighTemperature.results())
        println(lowestLowTemperature.results())
        println(rainyDaysCount.results())
        println(dayCount.results())
        println(allResults)
    }

    @Test
    fun `reuses mapping`() {
        val minTemperature = min<Int>()

        val maxTemperature = max<Int>()

        val dayCount = counter<DailyWeather>()

        val verySlowMapping = mapTo<DailyWeather, Int> { it -> it.low }

        val allResults = dailyWeather.consume(
            verySlowMapping.allOf(minTemperature, maxTemperature),
            dayCount)

        println(minTemperature.results())
        println(maxTemperature.results())
        println(dayCount.results())
        println(allResults)
    }
}
