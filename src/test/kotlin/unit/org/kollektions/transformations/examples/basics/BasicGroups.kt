package org.kollektions.transformations.examples.basics

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.count
import org.kollektions.dispatchers.allOf
import org.kollektions.dispatchers.groupBy
import org.kollektions.transformations.mapTo
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicGroups {
    private val things = listOf(Thing("Amber", "Circle"),
        Thing("Amber", "Square"),
        Thing("Red", "Oval"))

    @Test
    fun groups() {
        val actual = things
            .consume(
                groupBy(
                    keyFactory = { it: Thing -> it.color },
                    innerConsumerFactory = { count() }))
        assertEquals(mapOf("Amber" to 2L, "Red" to 1L), actual[0])
    }

    @Test
    fun `groups several consumers`() {
        val actual = things
            .consume(groupBy(keyFactory = { it: Thing -> it.color },
                innerConsumerFactory = { allOf(count(), mapTo { it: Thing -> it.shape }.asList()) }))
        assertEquals(mapOf("Amber" to listOf(2L, listOf("Circle", "Square")), "Red" to listOf(1L, listOf("Oval"))), actual[0])
    }

    @Test
    fun `nested groups`() {
        val things = listOf(Thing("Amber", "Circle"),
            Thing("Red", "Oval"))

        val actual = things.consume(
            groupBy(
                keyFactory = { a: Thing -> a.color },
                innerConsumerFactory = {
                    allOf(count(), groupBy(keyFactory = { a: Thing -> a.shape },
                        innerConsumerFactory = { count() }))
                })
        )

        val expected = mapOf("Amber" to listOf(1L, mapOf("Circle" to 1L)),
            "Red" to listOf(1L, mapOf("Oval" to 1L)))

        assertEquals(expected, actual[0])
    }

    data class Thing(val color: String, val shape: String)
}

