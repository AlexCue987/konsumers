package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.Branch
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.konsumers.dispatchers.branchOn
import com.tgt.trans.common.konsumers.resetters.ResetTrigger
import com.tgt.trans.common.konsumers.resetters.consumeWithResetting
import com.tgt.trans.common.konsumers.transformations.mapTo
import com.tgt.trans.common.konsumers.transformations.transformTo
import java.math.BigDecimal
import kotlin.test.Test


class GroceriesToBags {
    @Test
    fun `put groceries to bags`() {
        val notBaggedItemsConsumer = asList<GroceryItem>()

        val baggedItemsConsumer = consumeWithResetting(intermediateConsumerFactory= { asList<GroceryItem>()},
        resetTrigger = resetWhenExceedsWeightLimit(),
        intermediateResultsTransformer = {intermediateResults: Any,
                                          seriesDescription: Any -> intermediateResults as List<GroceryItem>},
        finalConsumer=asList<List<GroceryItem>>(),
        keepValueThatTriggeredReset = false,
        repeatLastValueInNewSeries = false)

        val actual = groceries.consume(Branch(condition = { item: GroceryItem -> item.weightPerItem <= MAX_WEIGHT},
            consumerForAccepted = baggedItemsConsumer,
            consumerForRejected = notBaggedItemsConsumer
            ))

        println("baggedItemsConsumer:")
        (baggedItemsConsumer.results() as List<*>).forEach {println("$it")}

        println("notBaggedItemsConsumer: ${notBaggedItemsConsumer.results()}")

    }

    private fun resetWhenExceedsWeightLimit() = ResetTrigger<GroceryItem>(
        stateFactory = { mapTo<GroceryItem, BigDecimal> { it.weightPerItem }.toSumOfBigDecimal() },
        stateType = ResetTrigger.StateType.After,
        condition = { state: Consumer<GroceryItem>, value: GroceryItem -> exceedsWeightLimit(state, value)},
        seriesDescriptor = { "Ignored" })

    private val MAX_WEIGHT = BigDecimal("8")

    private fun exceedsWeightLimit(state: Consumer<GroceryItem>, value: GroceryItem): Boolean {
        val weightInBag = (state.results() as BigDecimal)
        return weightInBag.add(value.weightPerItem) > MAX_WEIGHT
    }


    private val groceries = listOf(
        GroceryItem("Dozen Eggs", BigDecimal("1.5")),
        GroceryItem("Dozen Eggs", BigDecimal("1.5")),
        GroceryItem("Bread", BigDecimal("1")),
        GroceryItem("Pumpkin", MAX_WEIGHT),
        GroceryItem("Bread", BigDecimal("1")),
        GroceryItem("Milk", BigDecimal("9.5")),
        GroceryItem("Cheese", BigDecimal("2"))
        )

    data class GroceryItem(val name: String, val weightPerItem: BigDecimal)

    data class Bag(val items: List<GroceryItem>)
}
