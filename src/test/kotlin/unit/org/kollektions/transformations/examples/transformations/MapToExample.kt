package org.kollektions.transformations.examples.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.transformations.mapTo
import kotlin.test.Test
import kotlin.test.assertEquals

class MapToExample {

    data class OrderItem(val name: String, val quantity: Int)

    private val orderItems = listOf(
        OrderItem("Apple", 2),
        OrderItem("Orange", 3))

    @Test
    fun `just the names of items, without quantities`() {
        val names = orderItems.consume(
            mapTo<OrderItem, String> { it.name }.asList()
        )
        assertEquals(listOf("Apple", "Orange"), names[0])
    }

}
