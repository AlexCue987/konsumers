package com.tgt.trans.common.examples.extending

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.transformations.filterOn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse


class NewConsumer {
    private val sut = BitwiseAnd()

    @Test
    fun `basic example`() {
        val actual = listOf(1, 3).consume(BitwiseAnd())
        assertEquals(1, actual[0])
    }

    @Test
    fun `example with transformation`() {
        val actual = listOf(1, 3).consume(filterOn<Int> { it>0 }.bitwiseAnd())
        assertEquals(1, actual[0])
    }

    @Test
    fun `empty consumer has correct value`() {
        assertEquals(0, sut.results())
    }

    @Test
    fun `consumes one value`() {
        sut.process(42)
        assertEquals(42, sut.results())
    }

    @Test
    fun `consumes two values`() {
        sut.process(48)
        sut.process(17)
        assertEquals(16, sut.results())
    }

    @Test
    fun `works after transformation`() {
        val numbers = listOf(1, 3, 5, 4)
        val resultWithoutFilter = numbers.consume(BitwiseAnd())
        val expectedResultWithoutFilter = 0
        assertEquals(expectedResultWithoutFilter, resultWithoutFilter[0])
        val actual = numbers.consume(filterOn<Int> { it != 4 }.bitwiseAnd())
        val expectedResultWithFilter = 1
        assertFalse(expectedResultWithFilter == expectedResultWithoutFilter, "Filter should make a difference")
        assertEquals(expectedResultWithFilter, actual[0])
    }
}

class BitwiseAnd: Consumer<Int> {
    private var aggregate = Int.MAX_VALUE
    private var count = 0

    override fun process(value: Int) {
        aggregate = aggregate and value
        count++
    }

    override fun results(): Any = if(count == 0) 0 else aggregate
}


fun<T> ConsumerBuilder<T, Int>.bitwiseAnd() = this.build(BitwiseAnd())
