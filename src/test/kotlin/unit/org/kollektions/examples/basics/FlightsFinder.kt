package org.kollektions.examples.basics

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.bottomNBy
import org.kollektions.consumers.consume
import org.kollektions.transformations.filterOn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
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

    @Test
    fun `wrap search results in a data class`() {
        val cheapestOnSaturdayPlanA = filterOn<Flight> { it.arrival.toLocalDate() == saturday }
            .bottomNBy(1) { it: Flight -> it.price }

        val earliestAfterSaturdayPlanB = filterOn<Flight> { it.arrival.toLocalDate() > saturday }
            .bottomNBy(1) { it: Flight -> it.arrival }

        val actual = flights.consume(
            {consumers: List<Consumer<Flight>> -> getFlightSearchResults(consumers)},
            cheapestOnSaturdayPlanA,
            earliestAfterSaturdayPlanB)

        println(actual)

        val expected = FlightSearchResults(Optional.of(cheapestOnSaturday), Optional.of(earliestAfterSaturday))
        assertEquals(expected, actual)
    }

    private data class FlightSearchResults(val cheapestOnSaturday: Optional<Flight>,
                                           val earliestAfterSaturday: Optional<Flight>)

    private fun getFlightSearchResults(consumers: List<Consumer<Flight>>): FlightSearchResults {
        val cheapestOnSaturday = listToOptional(consumers[0].results() as List<List<Flight>>)
        val earliestAfterSaturday = listToOptional(consumers[1].results() as List<List<Flight>>)
        return FlightSearchResults(cheapestOnSaturday, earliestAfterSaturday)
    }

    private fun<T> listToOptional(list: List<List<T>>) =
        if (list.isEmpty() || list[0].isEmpty()) Optional.empty() else Optional.of(list[0][0])
}
