package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.consumeByOne
import com.tgt.trans.common.konsumers.transformations.transformTo
import kotlin.test.Test
import kotlin.test.assertEquals

class UnpackItems {

    @Test
    fun `divide too heavy items if possible`() {
        val itemsOfFivePoundsOrLess = items.consumeByOne(
            transformTo<Item, WeightedItem> { item: Item -> unpack(item) }
                .asList()
        )
        println(itemsOfFivePoundsOrLess)

        /* Output:
Rejecting item: Item(name=pumpkin, canDivide=false)
WeightedItem(name=melon, weight=5)
WeightedItem(name=cheese, weight=5)
WeightedItem(name=cheese, weight=1)
WeightedItem(name=Bread, weight=1)
         */

        val expected = listOf(
            WeightedItem(name = "melon", weight = 5),
            WeightedItem(name = "cheese", weight = 5),
            WeightedItem(name = "cheese", weight = 1),
            WeightedItem(name = "bread", weight = 1)
        )

        assertEquals(expected, itemsOfFivePoundsOrLess)
    }

    private val MAX_WEIGHT = 5

    private fun unpack(item: Item): Sequence<WeightedItem> {
        var weight = getWeightByExpensiveApiCall(item.name)
        if (weight > MAX_WEIGHT && !item.canDivide) {
            println("Rejecting item: $item")
            return sequenceOf()
        }
        // TODO: use sequence
        val ret = mutableListOf<WeightedItem>()
        while(weight > MAX_WEIGHT) {
            ret.add(WeightedItem(item.name, MAX_WEIGHT))
            weight -= MAX_WEIGHT
        }
        ret.add(WeightedItem(item.name, weight))
        return ret.asSequence()
    }

    private data class Item(val name: String, val canDivide: Boolean)

    private val items = listOf(
        Item("melon", false),
        Item("cheese", true),
        Item("pumpkin", false),
        Item("bread", false)
    )

    private data class WeightedItem(val name: String, val weight: Int)

    fun getWeightByExpensiveApiCall(itemName: String) = when(itemName) {
        "melon" -> 5
        "cheese" -> 6
        "pumpkin" -> 20
        else -> 1
    }
}
