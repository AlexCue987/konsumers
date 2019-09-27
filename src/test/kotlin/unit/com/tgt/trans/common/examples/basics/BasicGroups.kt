package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.count
import com.tgt.trans.common.aggregator2.consumers.counter
import com.tgt.trans.common.aggregator2.decorators.allOf
import com.tgt.trans.common.aggregator2.decorators.groupBy
import com.tgt.trans.common.aggregator2.decorators.mapTo
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicGroups {
    private val things = listOf(Thing("Amber", "Circle"),
        Thing("Amber", "Square"),
        Thing("Red", "Oval"))

    @Test
    fun groups() {
        val actual = things
                .consume(groupBy(keyFactory =  { it: Thing -> it.color },
                    innerConsumerFactory = { counter() }))
        assertEquals(mapOf("Amber" to 2L, "Red" to 1L), actual[0])
    }

    @Test
    fun `groups several consumers`() {
        val actual = things
            .consume(groupBy(keyFactory =  { it: Thing -> it.color },
                innerConsumerFactory = { allOf(counter(), mapTo { it: Thing -> it.shape }.asList()) }))
        assertEquals(mapOf("Amber" to listOf(2L, listOf("Circle", "Square")), "Red" to listOf(1L, listOf("Oval"))), actual[0])
    }

    @Test
    fun `nested groups`() {
        val things = listOf(Thing("Amber", "Circle"),
            Thing("Red", "Oval"))
        val actual = things.consume(groupBy(keyFactory = { a: Thing -> a.color },
            innerConsumerFactory = {
                allOf(counter(), groupBy(keyFactory = { a: Thing -> a.shape },
                    innerConsumerFactory = { allOf(counter()) }))
            })
        )
        val expected = mapOf("Amber" to listOf(1L, mapOf("Circle" to listOf(1L))),
            "Red" to listOf(1L, mapOf("Oval" to listOf(1L))))
        assertEquals(expected, actual[0])
    }
    data class Thing(val color: String, val shape: String)
}

