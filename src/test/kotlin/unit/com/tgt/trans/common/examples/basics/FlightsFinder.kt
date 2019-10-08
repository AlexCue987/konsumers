package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.bottomNBy
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.transformations.filterOn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class FlightsFinder {
    data class Flight(val arrival: LocalDateTime, val price: BigDecimal)

    private val friday = LocalDate.of(2019, 10, 11)
    private val saturday = friday.plusDays(1)
    private val sunday = friday.plusDays(2)

    val cheapestOnSaturday = Flight(saturday.atTime(11, 45), BigDecimal.valueOf(100L))
    val earliestAfterSaturday = Flight(sunday.atTime(8, 45), BigDecimal.valueOf(410L))
    val flights = listOf(
        Flight(friday.atTime(14, 45), BigDecimal.valueOf(175L)),
        Flight(friday.atTime(19, 0), BigDecimal.valueOf(300L)),
        cheapestOnSaturday,
        Flight(saturday.atTime(14, 14), BigDecimal.valueOf(175L)),
        earliestAfterSaturday,
        Flight(sunday.atTime(14, 45), BigDecimal.valueOf(175L))
    )

    @Test
    fun `find flights for plans A and B`() {
        val cheapestOnSaturdayPlanA = filterOn<Flight> { it.arrival.toLocalDate() == saturday }
            .bottomNBy(1) { it: Flight -> it.price }

        val earliestAfterSaturdayPlanB = filterOn<Flight> { it.arrival.toLocalDate() > saturday }
            .bottomNBy(1) { it: Flight -> it.arrival }

        val actual = flights.consume(cheapestOnSaturdayPlanA, earliestAfterSaturdayPlanB)

        println(cheapestOnSaturdayPlanA.results())
        println(earliestAfterSaturdayPlanB.results())

        assertEquals(listOf(listOf(listOf(cheapestOnSaturday)), listOf(listOf(earliestAfterSaturday))), actual)
    }
}
