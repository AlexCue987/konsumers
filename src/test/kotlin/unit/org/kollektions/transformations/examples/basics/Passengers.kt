package org.kollektions.transformations.examples.basics

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.dispatchers.Branch
import org.kollektions.transformations.filterOn
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

class Passengers {

    data class Passenger(val name: String, val destination: String)

    val Yoda = Passenger("Yoda", "Tattoine")
    val R2D2 = Passenger("R2D2", "Alderaan")
    val HanSolo = Passenger("Han Solo", "Alderaan")
    val Chewbacca = Passenger("Chewbacca", "Tattoine")
    val passengers = listOf(
        Yoda,
        R2D2,
        HanSolo,
        Chewbacca
    )

    @Test
    fun `code using two filters is repetitive, difficult to maintain, our intent is not clear`() {
        val spaceportName = "Tattoine"
        val actual = passengers.consume(
            filterOn{ it: Passenger -> it.destination == spaceportName }.asList(),
            filterOn{ it: Passenger -> it.destination != spaceportName }.asList()
            )

        assertAll(
            { assertEquals(listOf(Yoda, Chewbacca), actual[0])},
            { assertEquals(listOf(R2D2, HanSolo), actual[1])}
        )
    }

    @Test
    fun `passengers leaving spaceport or transferring to another flight`() {
        val leavingSpaceport = asList<Passenger>()
        val transferringToAnotherFlight = asList<Passenger>()
        val spaceportName = "Tattoine"
        passengers.consume(Branch({ it: Passenger -> it.destination == spaceportName },
            consumerForAccepted = leavingSpaceport,
            consumerForRejected = transferringToAnotherFlight))

        println("Left spaceport: ${leavingSpaceport.results()}")
        println("Transferred: ${transferringToAnotherFlight.results()}")

        assertAll(
            { assertEquals(listOf(Yoda, Chewbacca), leavingSpaceport.results())},
            { assertEquals(listOf(R2D2, HanSolo), transferringToAnotherFlight.results())}
        )
    }
}
