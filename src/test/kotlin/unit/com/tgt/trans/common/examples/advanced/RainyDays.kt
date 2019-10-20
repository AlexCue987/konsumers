package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.examples.DailyWeather
import com.tgt.trans.common.examples.dailyWeather
import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.dispatchers.consumeWithResetting
import com.tgt.trans.common.konsumers.transformations.filterOn
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class RainyDays {
    @Test
    fun `series of consecutive rainy days`() {
        val longestSeriesOfRainyDaysConsumer =
            { topBy { it: List<DailyWeather> -> it.size } }

        val seriesWithLargestTotalRainfallConsumer =
            { topBy { it: List<DailyWeather> -> it.map { it.rainAmount.toDouble() }.sum() } }

        val seriesWithLargestRainfallInOneDayConsumer =
            { topBy { it: List<DailyWeather> -> it.map { it.rainAmount.toDouble() }.max()!! } }

        val actual = dailyWeather.consume(
            filterOn<DailyWeather> { it.rainAmount > BigDecimal.ZERO }
                .consumeWithResetting(
                    intermediateConsumersFactory = {
                        listOf(asList<DailyWeather>(), Last<DailyWeather>())
                    },
                    resetTrigger = { intermediateConsumers: List<Consumer<DailyWeather>>, value: DailyWeather ->
                        val previousDay = (intermediateConsumers[1] as Last<DailyWeather>).results()
                        previousDay.isPresent && previousDay.get().day.plusDays(1) < value.day
                    },
                    intermediateResultsTransformer = { intermediateConsumers: List<Consumer<DailyWeather>> ->
                        intermediateConsumers[0].results() as List<DailyWeather>
                    },
                    finalConsumer = allOf(
                        longestSeriesOfRainyDaysConsumer(),
                        seriesWithLargestTotalRainfallConsumer(),
                        seriesWithLargestRainfallInOneDayConsumer()
                    )))

        val unwrappedResults = (actual[0] as List<List<List<DailyWeather>>>)

        println("longestSeriesOfRainyDays: ${unwrappedResults[0][0]}")
        println("seriesWithLargestTotalRainfall: ${unwrappedResults[1][0]}")
        println("seriesLargestRainfallInOneDay: ${unwrappedResults[2][0]}")

        assertEquals(listOf(dailyWeather.subList(1, 4)),
            unwrappedResults[0],
            "longestSeriesOfRainyDays")

        assertEquals(listOf(dailyWeather.subList(10, 12)),
            unwrappedResults[1],
            "seriesWithLargestTotalRainfall")

        assertEquals(listOf(dailyWeather.subList(7, 8), dailyWeather.subList(10, 12)),
            unwrappedResults[2],
            "seriesLargestRainfallInOneDay")
    }
}
