package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.count
import io.mockk.*
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.Test

class MapToConsumerTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>()
                .consume(mapTo<Int, BigDecimal> { BigDecimal.valueOf(it.toLong()) }
                        .count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val actual = listOf(42)
                .consume(mapTo<Int, BigDecimal> { BigDecimal.valueOf(it.toLong()) }
                        .count())
        assertEquals(1L, actual[0])
    }

    @Test
    fun chainsToAnotherBuilder() {
        val actual = listOf(1, 2, 3)
                .consume(
                        filterOn { a: Int -> a != 2 }
                                .mapTo { BigDecimal.valueOf(it.toLong()) }
                                .count())
        assertEquals(2L, actual[0])
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<BigDecimal>> {
            every { stop() } just Runs
        }
        val sut = mapTo<Int, BigDecimal> { BigDecimal.valueOf(it.toLong()) }.build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
