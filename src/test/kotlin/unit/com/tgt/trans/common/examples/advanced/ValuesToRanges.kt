package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.resetters.ResetTrigger
import com.tgt.trans.common.konsumers.resetters.consumeWithResetting
import java.time.LocalDateTime
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
        val actual = prices.consume(
            consumeWithResetting(
                intermediateConsumerFactory = { allOf<TimedPrice>(FirstN(1), LastN(1))},
                resetTrigger = resetWhenValueChanges(),
                intermediateResultsTransformer = { intermediateResults: Any,
                                                   seriesDescription: Any ->
                    getPriceRange(intermediateResults)
                },
                finalConsumer = asList(),
                keepValueThatTriggeredReset = true,
                repeatLastValueInNewSeries = true)
        )

        (actual[0] as List<*>).forEach { println(it) }
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

        assertEquals(expected, actual[0])
    }

    private fun getPriceRange(intermediateResults: Any): PriceRange {
        val intermediateResultsList = (intermediateResults as List<*>)
        val firstPrice = (intermediateResultsList[0] as List<TimedPrice>)[0]
        val lastPrice = (intermediateResultsList[1] as List<TimedPrice>)[0]
        return PriceRange(firstPrice.takenAt, lastPrice.takenAt, firstPrice.price)
    }

    private fun resetWhenValueChanges() = ResetTrigger<TimedPrice>(
        stateFactory = { FirstN<TimedPrice>(1) },
        stateType = ResetTrigger.StateType.After,
        condition = { state: Consumer<TimedPrice>, value: TimedPrice ->
            val stateResults = (state.results() as List<TimedPrice>)
            !stateResults.isEmpty() && stateResults[0].price != value.price},
        seriesDescriptor = { "Ignored" })

}
