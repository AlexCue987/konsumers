package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.transformations.filterOn
import com.tgt.trans.common.konsumers.transformations.mapTo
import com.tgt.trans.common.examples.DailyWeather
import com.tgt.trans.common.examples.WeatherType
import com.tgt.trans.common.examples.dailyWeather
import kotlin.test.Test


class MultipleResultsAtOnce {
    @Test
    fun `computes multiple results in one pass`() {
        val warmestDay = topNBy(count = 1) { it: DailyWeather -> it.high}

        val lowestTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val allResults = dailyWeather.consume(warmestDay, lowestTemperature)

        //one day with high=75
        println(warmestDay.results())

        //two days with low=20
        println(lowestTemperature.results())

        // all results returned in a list
        println(allResults)
    }
}
