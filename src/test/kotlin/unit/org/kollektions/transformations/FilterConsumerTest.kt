package org.kollektions.transformations

import io.mockk.*
import org.kollektions.consumers.Consumer
import org.kollektions.consumers.consume
import org.kollektions.consumers.count
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.Test

class FilterConsumerTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val actual = listOf(42).consume(filterOn { a: Int -> a > 0 }.count())
        assertEquals(1L, actual[0])
    }

    @Test
    fun chainsAfterAnotherBuilder() {
        val sut = mapTo { a: Int -> BigDecimal(a.toLong()).times(BigDecimal("0.5")) }
                .filterOn {a: BigDecimal -> a > BigDecimal("0.75")  }
                .count()
        val actual = listOf(1, 2, 3).consume(sut)
        assertEquals(2L, actual[0])
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<Int>> {
            every { stop() } just Runs
        }
        val sut = filterOn<Int> { true }.build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
