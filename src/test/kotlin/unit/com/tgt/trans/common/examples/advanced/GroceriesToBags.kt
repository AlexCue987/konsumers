package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.Branch
import com.tgt.trans.common.konsumers.resetters.ResetTrigger
import com.tgt.trans.common.konsumers.resetters.consumeWithResetting
import com.tgt.trans.common.konsumers.transformations.mapTo
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals


class GroceriesToBags {
    @Test
    fun `put groceries to bags`() {
        val notBaggedItemsConsumer = asList<GroceryItem>()

        val baggedItemsConsumer = consumeWithResetting(intermediateConsumerFactory = { asList<GroceryItem>() },
            resetTrigger = resetWhenExceedsWeightLimit(),
            intermediateResultsTransformer = { intermediateResults: Any,
                                               seriesDescription: Any ->
                intermediateResults as List<GroceryItem>
            },
            finalConsumer = asList<List<GroceryItem>>(),
            keepValueThatTriggeredReset = false,
            repeatLastValueInNewSeries = false)

        val actual = groceries.consume(Branch(condition = { item: GroceryItem -> item.weightPerItem <= MAX_WEIGHT },
            consumerForAccepted = baggedItemsConsumer,
            consumerForRejected = notBaggedItemsConsumer
        ))

        println("baggedItemsConsumer:")
        (baggedItemsConsumer.results() as List<*>).forEach { println("$it") }
        /* the output

    baggedItemsConsumer:
    [GroceryItem(name=Dozen Eggs, weightPerItem=1.5), GroceryItem(name=Dozen Eggs, weightPerItem=1.5), GroceryItem(name=Bread, weightPerItem=1)]
    [GroceryItem(name=Pumpkin, weightPerItem=8)]
    [GroceryItem(name=Bread, weightPerItem=1), GroceryItem(name=Cheese, weightPerItem=2)]

         */

        assertEquals(listOf(
                listOf(dozenEggs, dozenEggs, bread),
                listOf(pumpkin),
                listOf(bread, cheese)
            ),
            baggedItemsConsumer.results())

        println("notBaggedItemsConsumer: ${notBaggedItemsConsumer.results()}")
        /* the output

    notBaggedItemsConsumer: [GroceryItem(name=Milk, weightPerItem=9.5)]

         */

        assertEquals(listOf(milk), notBaggedItemsConsumer.results())
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


    val dozenEggs = GroceryItem("Dozen Eggs", BigDecimal("1.5"))
    val bread = GroceryItem("Bread", BigDecimal("1"))
    val pumpkin = GroceryItem("Pumpkin", MAX_WEIGHT)
    val milk = GroceryItem("Milk", BigDecimal("9.5"))
    val cheese = GroceryItem("Cheese", BigDecimal("2"))
    private val groceries = listOf(
        dozenEggs,
        dozenEggs,
        bread,
        pumpkin,
        bread,
        milk,
        cheese
        )

    data class GroceryItem(val name: String, val weightPerItem: BigDecimal)
}
