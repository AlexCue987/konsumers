package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.topNBy
import com.tgt.trans.common.konsumers.transformations.filterOn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test

class FlightsFinder {
    data class Flight(val arrival: LocalDateTime, val price: BigDecimal)

    private val friday = LocalDate.of(2019, 10, 11)
    private val saturday = friday.plusDays(1)
    private val sunday = friday.plusDays(2)

    val flights = listOf(
        Flight(friday.atTime(14, 45), BigDecimal.valueOf(175L)),
        Flight(friday.atTime(19, 0), BigDecimal.valueOf(300L)),
        Flight(saturday.atTime(11, 45), BigDecimal.valueOf(100L)),
        Flight(saturday.atTime(14, 14), BigDecimal.valueOf(175L)),
        Flight(sunday.atTime(8, 45), BigDecimal.valueOf(410L)),
        Flight(sunday.atTime(14, 45), BigDecimal.valueOf(175L))
    )

    @Test
    fun `find flights`() {
        val cheapestOnSaturday = filterOn<Flight> { it.arrival.toLocalDate() == saturday }
            .topNBy(1) { it: Flight -> -it.price }
    }
}
