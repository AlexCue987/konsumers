package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.max
import com.tgt.trans.common.konsumers.consumers.min
import com.tgt.trans.common.konsumers.transformations.allOf
import com.tgt.trans.common.konsumers.transformations.branchOn
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class IfOrElse {
    @Test
    fun `handles filtered out items`() {
        val alertOnVeryHighTemperature = asList<Int>()
        val minimumTemperature = min<Int>()
        val maximumTemperature = max<Int>()
        val sut = branchOn(condition = { a: Int -> a < 200 },
            consumerForRejected = alertOnVeryHighTemperature).allOf(minimumTemperature, maximumTemperature)
        val veryHighTemperature = 201
        val actual = listOf(75, 82, 55, veryHighTemperature, 74).consume(sut)
        println("minimumTemperature: ${minimumTemperature.results()}")
        println("maximumTemperature: ${maximumTemperature.results()}")
        println("Rejected items: ${alertOnVeryHighTemperature.results()}")
        assertEquals(Optional.of(55), minimumTemperature.results())
        assertEquals(Optional.of(82), maximumTemperature.results())
        assertEquals(listOf(veryHighTemperature), alertOnVeryHighTemperature.results())
    }
}
