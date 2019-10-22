package org.kollektions.transformations.examples.advanced

import org.kollektions.transformations.examples.DailyWeather
import org.kollektions.transformations.examples.dailyWeather
import org.kollektions.consumers.*
import org.kollektions.dispatchers.allOf
import org.kollektions.dispatchers.consumeWithResetting
import org.kollektions.transformations.filterOn
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class RainyDays {
    @Test
    fun `series of consecutive rainy days`() {
        val longestSeriesOfRainyDaysConsumer =
            topBy { it: List<DailyWeather> -> it.size }

        val seriesWithLargestTotalRainfallConsumer =
            topBy { it: List<DailyWeather> -> it.map { it.rainAmount.toDouble() }.sum() }

        val seriesWithLargestRainfallInOneDayConsumer =
            topBy { it: List<DailyWeather> -> it.map { it.rainAmount.toDouble() }.max()!! }

        dailyWeather.consumeByOne(
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
                        longestSeriesOfRainyDaysConsumer,
                        seriesWithLargestTotalRainfallConsumer,
                        seriesWithLargestRainfallInOneDayConsumer
                    )))

        println("longestSeriesOfRainyDays: ${longestSeriesOfRainyDaysConsumer.results()}")
        println("seriesWithLargestTotalRainfall: ${seriesWithLargestTotalRainfallConsumer.results()}")
        println("seriesLargestRainfallInOneDay: ${seriesWithLargestRainfallInOneDayConsumer.results()}")

        assertEquals(listOf(dailyWeather.subList(1, 4)),
            longestSeriesOfRainyDaysConsumer.results(),
            "longestSeriesOfRainyDays")

        assertEquals(listOf(dailyWeather.subList(10, 12)),
            seriesWithLargestTotalRainfallConsumer.results(),
            "seriesWithLargestTotalRainfall")

        assertEquals(listOf(dailyWeather.subList(7, 8), dailyWeather.subList(10, 12)),
            seriesWithLargestRainfallInOneDayConsumer.results(),
            "seriesLargestRainfallInOneDay")
    }
}
