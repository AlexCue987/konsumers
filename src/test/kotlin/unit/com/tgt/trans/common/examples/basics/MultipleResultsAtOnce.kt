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
        val highestTemperatureOnSunnyDay = filterOn<DailyWeather> { it.weatherType == WeatherType.Sunny }
            .mapTo { it -> it.high }
            .max()

        val lowestTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val dayCount = count<DailyWeather>()

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
