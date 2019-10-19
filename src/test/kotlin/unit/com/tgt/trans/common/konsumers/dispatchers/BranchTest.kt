package com.tgt.trans.common.konsumers.dispatchers

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.testutils.FakeStopTester
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BranchTest {
    private val rejected = asList<Int>()
    val sut = branchOn(condition = { a: Int -> a > 0 },
        consumerForRejected = rejected).asList()

    @Test
    fun `handles both empty`() {
        val actual = listOf<Int>().consume(sut)
        assertEquals(listOf(listOf<Int>(), listOf<Int>()), actual[0])
    }

    @Test
    fun `handles accepted empty`() {
        val actual = listOf(-1).consume(sut)
        assertEquals(listOf(listOf<Int>(), listOf<Int>(-1)), actual[0])
    }

    @Test
    fun `handles rejected empty`() {
        val actual = listOf(1).consume(sut)
        assertEquals(listOf(listOf<Int>(1), listOf<Int>()), actual[0])
    }

    @Test
    fun `handles both not empty`() {
        val actual = listOf(1, -1).consume(sut)
        assertEquals(listOf(listOf<Int>(1), listOf<Int>(-1)), actual[0])
    }

    @Test
    fun `passes stop downstream`() {
        val acceptedBranch = FakeStopTester<Int>()
        val rejectedBranch = FakeStopTester<Int>()
        val sut = Branch<Int>(
            condition = { a: Int -> a > 0 },
            consumerForAccepted = acceptedBranch,
            consumerForRejected = rejectedBranch)
        sut.stop()
        assertAll(
            { assertTrue(acceptedBranch.isStopped()) },
            { assertTrue(rejectedBranch.isStopped()) }
        )
    }
}
