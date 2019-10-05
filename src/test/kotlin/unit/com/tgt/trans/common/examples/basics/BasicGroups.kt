package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.count
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.dispatchers.groupBy
import com.tgt.trans.common.konsumers.transformations.mapTo
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
                        innerConsumerFactory = { allOf(count()) }))
                })
        )

        val expected = mapOf("Amber" to listOf(1L, mapOf("Circle" to listOf(1L))),
            "Red" to listOf(1L, mapOf("Oval" to listOf(1L))))

        assertEquals(expected, actual[0])
    }

    data class Thing(val color: String, val shape: String)
}

