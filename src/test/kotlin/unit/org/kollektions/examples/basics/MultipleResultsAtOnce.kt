package org.kollektions.examples.basics

import org.kollektions.consumers.consume
import org.kollektions.consumers.min
import org.kollektions.consumers.topNBy
import org.kollektions.transformations.mapTo
import org.kollektions.examples.DailyWeather
import org.kollektions.examples.dailyWeather
import kotlin.test.Test


class MultipleResultsAtOnce {
    @Test
    fun `computes multiple results in one pass`() {
        val warmestDay = topNBy(count = 1) { it: DailyWeather -> it.high }

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
