package com.tgt.trans.common.konsumers.dispatchers

import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.max
import com.tgt.trans.common.konsumers.consumers.min
import com.tgt.trans.common.testutils.FakeStopTester
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ListOfConsumersTest {
    private val sut = allOf<Int>(min(), max())

    @Test
    fun `handles empty`() {
        val actual = listOf<Int>().consume(sut)
        val expected = listOf(Optional.empty<Int>(), Optional.empty<Int>())
        assertEquals(expected, actual[0])
    }

    @Test
    fun `handles non-empty`() {
        val actual = listOf(1, 2).consume(sut)
        val expected = listOf(Optional.of(1), Optional.of(2))
        assertEquals(expected, actual[0])
    }

    @Test
    fun `passes stop downstream`() {
        val sut = allOf<Int>(FakeStopTester(), FakeStopTester())
        sut.stop()
        val actual = sut.results()
        assertEquals(listOf(true, true), actual)
    }
}
