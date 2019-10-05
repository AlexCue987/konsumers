package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.count
import com.tgt.trans.common.konsumers.consumers.max
import com.tgt.trans.common.konsumers.consumers.min
import com.tgt.trans.common.konsumers.dispatchers.allOf
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ListOfConsumersTest {
    @Test
    fun computesSeveral() {
        val actual = listOf(1, 2, 3, 4, 3).consume(
            allOf(min(), max(), count())
        )
        assertEquals(listOf(Optional.of(1), Optional.of(4), 5L), actual[0])
    }

    @Test
    fun filtersAndComputesSeveral() {
        val actual = listOf(1, 2, 3, 4, 3).consume(
                filterOn<Int> { it < 4 }.allOf(min(), max(), count()),
                filterOn<Int> { it > 2 }.allOf(min(), max(), count()),
                mapTo<Int, Int> { it * 2 }.filterOn { it > 3 }.allOf(min(), max(), count())
        )
        assertEquals(listOf(
                listOf(Optional.of(1), Optional.of(3), 4L),
                listOf(Optional.of(3), Optional.of(4), 3L),
                listOf(Optional.of(4), Optional.of(8), 4L)),
                actual)
    }

    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(
            allOf(min(), max(), count())
        )
        assertEquals(listOf(Optional.empty<Int>(), Optional.empty<Int>(), 0L), actual[0])
    }

    @Test
    fun handlesOneItem() {
        val element = 42
        val actual = listOf(element).consume(
            allOf(min(), max(), count())
        )
        assertEquals(listOf(Optional.of(element), Optional.of(element), 1L), actual[0])
    }
}
