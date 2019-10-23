package org.kollektions.examples.advanced

import org.kollektions.consumers.*
import org.kollektions.dispatchers.consumeWithResetting
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ValuesToRanges {
    val wednesdayMorning = LocalDateTime.of(2019, 10, 9, 8, 7)
    val wednesdayNoon = LocalDateTime.of(2019, 10, 9, 12, 7)
    val wednesdayNight = LocalDateTime.of(2019, 10, 9, 19, 7)
    val midnight = LocalDateTime.of(2019, 10, 9, 23, 59)
    val thursdayMorning = LocalDateTime.of(2019, 10, 10, 5, 7)
    val thursdayNight = LocalDateTime.of(2019, 10, 10, 18, 7)

    private val prices = listOf(
        TimedPrice(wednesdayMorning, 44),
        TimedPrice(wednesdayNoon, 44),
        TimedPrice(wednesdayNight, 46),
        TimedPrice(midnight, 48),
        TimedPrice(thursdayMorning, 42),
        TimedPrice(thursdayNight, 42)
    )

    private data class TimedPrice(val takenAt: LocalDateTime, val price: Int)

    private data class PriceRange(val startAt: LocalDateTime, val endAt: LocalDateTime, val price: Int)

    @Test
    fun `coalesce prices to intervals`() {
        val actual = prices.consumeByOne(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(First(), Last()) },
                resetTrigger = { intermediateConsumers: List<Consumer<TimedPrice>>, value: TimedPrice ->
                    val stateResults = (intermediateConsumers[0].results() as Optional<TimedPrice>)
                    stateResults.isPresent && stateResults.get().price != value.price
                },
                intermediateResultsTransformer = { intermediateConsumers: List<Consumer<TimedPrice>> ->
                    getPriceRange(intermediateConsumers)
                },
                finalConsumer = asList(),
                keepValueThatTriggeredReset = true,
                repeatLastValueInNewSeries = true)
        )

        println(actual)
        /* Output:
PriceRange(startAt=2019-10-09T08:07, endAt=2019-10-09T15:07, price=44)
PriceRange(startAt=2019-10-09T15:07, endAt=2019-10-09T23:59, price=46)
PriceRange(startAt=2019-10-09T23:59, endAt=2019-10-10T05:07, price=48)
PriceRange(startAt=2019-10-10T05:07, endAt=2019-10-10T11:07, price=42)
        */

        val expected = listOf(
            PriceRange(wednesdayMorning, wednesdayNight, 44),
            PriceRange(wednesdayNight, midnight, 46),
            PriceRange(midnight, thursdayMorning, 48),
            PriceRange(thursdayMorning, thursdayNight, 42)
        )

        assertEquals(expected, actual)
    }

    private fun getPriceRange(intermediateConsumers: List<Consumer<TimedPrice>>): PriceRange {
        val firstPrice = (intermediateConsumers[0].results() as Optional<TimedPrice>).get()
        val lastPrice = (intermediateConsumers[1].results() as Optional<TimedPrice>).get()
        return PriceRange(firstPrice.takenAt, lastPrice.takenAt, firstPrice.price)
    }
}
