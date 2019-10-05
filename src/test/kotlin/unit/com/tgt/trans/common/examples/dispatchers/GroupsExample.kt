package com.tgt.trans.common.examples.dispatchers

import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.count
import com.tgt.trans.common.konsumers.dispatchers.groupBy
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupsExample {
    private val things = listOf(Thing("Amber", "Circle"),
        Thing("Amber", "Square"),
        Thing("Red", "Oval"))

    @Test
    fun groups() {
        val actual = things
            .consume(
                groupBy(
                    keyFactory = { it: Thing -> it.color },
                    innerConsumerFactory = { count() }
                )
            )

        assertEquals(mapOf("Amber" to 2L, "Red" to 1L), actual[0])
    }

    data class Thing(val color: String, val shape: String)
}

