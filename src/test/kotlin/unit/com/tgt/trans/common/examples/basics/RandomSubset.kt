package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.conditions.Condition
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.peek
import kotlin.test.Test

class RandomSubsetTest {

    private fun indexIsInRandomSubset(i: Int) = (i==2)

    @Test
    fun `traditional approach, create short-lived IndexedValue objects to dispose of after filtering`() {
        val items = (1..5).asSequence()
            .map { "Item $it" }
            .toList()

        items
            .asSequence()
            .onEach { println("Processing $it")}
            .withIndex()
            .onEach { println("Processing $it")}
            .filter { indexIsInRandomSubset(it.index) }
            .map { it.value }
            .forEach { println("After filtering: $it") }
    }

    @Test
    fun `transformation uses a State and does not need intermediate objects`() {
        val items = (1..5).asSequence()
            .map { "Item $it" }
            .toList()

        val actual = items.consume(
            conditionOnIndex<String> { index: Int -> indexIsInRandomSubset(index) }
                .peek { println(it) }
                .asList())

        print(actual)
    }
}

fun<T> indexMeetsCondition(conditionOnIndex: (index: Int) -> Boolean): Condition<T> {
    val index = Index<T>()
    return ConditionOnState(state = index, condition = {stateValue: Int, incomingValue: T -> conditionOnIndex(stateValue)})
}
