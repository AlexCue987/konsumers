package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.dispatchers.Branch
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
    fun `passengers leaving spaceport or transferring to another flight`() {
        val leavingSpaceport = asList<Passenger>()
        val transferringToAnotherFlight = asList<Passenger>()
        passengers.consume(Branch({ it: Passenger -> it.destination == "Tattoine" },
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
