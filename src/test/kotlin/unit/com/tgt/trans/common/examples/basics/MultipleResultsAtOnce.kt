package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.*
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.mapTo
import com.tgt.trans.common.examples.DailyWeather
import com.tgt.trans.common.examples.WeatherType
import com.tgt.trans.common.examples.dailyWeather
import kotlin.test.Test


class MultipleResultsAtOnce {
    @Test
    fun `computes multiple results in one pass`() {
        val highestTemperatureOnSunnyDay = filterOn<DailyWeather> { it.weatherType == WeatherType.Sunny }
            .mapTo { it -> it.high }
            .max()

        val lowestTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val dayCount = counter<DailyWeather>()

        val allResults = dailyWeather.consume(highestTemperatureOnSunnyDay, lowestTemperature, dayCount)

        //one day with high=75
        println(highestTemperatureOnSunnyDay.results())
        //two days with low=20
        println(lowestTemperature.results())
        //twelve days
        println(dayCount.results())
        println(allResults)
    }
}