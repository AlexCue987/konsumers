package org.kollektions.transformations.examples.advanced

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.toSumOfBigDecimal
import org.kollektions.dispatchers.Branch
import org.kollektions.dispatchers.consumeWithResetting
import org.kollektions.transformations.mapTo
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals


class GroceriesToBags {
    @Test
    fun `put groceries to bags`() {
        val notBaggedItemsConsumer = asList<GroceryItem>()

        val baggedItemsConsumer = consumeWithResetting(
            intermediateConsumersFactory = {
                val itemsInBag = asList<GroceryItem>()
                val bagWeight = mapTo<GroceryItem, BigDecimal> { it.weight }.toSumOfBigDecimal()
                listOf(itemsInBag, bagWeight)
            },
            resetTrigger = { intermediateConsumers: List<Consumer<GroceryItem>>, value: GroceryItem ->
                exceedsWeightLimit(intermediateConsumers, value)
            },
            intermediateResultsTransformer = { intermediateConsumers: List<Consumer<GroceryItem>> ->
                intermediateConsumers[0].results() as List<GroceryItem>
            },
            finalConsumer = asList<List<GroceryItem>>(),
            keepValueThatTriggeredReset = false,
            repeatLastValueInNewSeries = false)

        groceries.consume(Branch(condition = { item: GroceryItem -> item.weight <= MAX_WEIGHT },
            consumerForAccepted = baggedItemsConsumer,
            consumerForRejected = notBaggedItemsConsumer
        ))

        println("baggedItemsConsumer:")
        (baggedItemsConsumer.results() as List<*>).forEach { println("$it") }
        /* the output

    baggedItemsConsumer:
    [GroceryItem(name=Dozen Eggs, weight=1.5), GroceryItem(name=Dozen Eggs, weight=1.5), GroceryItem(name=Bread, weight=1)]
    [GroceryItem(name=Pumpkin, weight=8)]
    [GroceryItem(name=Bread, weight=1), GroceryItem(name=Cheese, weight=2)]

         */

        assertEquals(listOf(
                listOf(dozenEggs, dozenEggs, bread),
                listOf(pumpkin),
                listOf(bread, cheese)
            ),
            baggedItemsConsumer.results())

        println("notBaggedItemsConsumer: ${notBaggedItemsConsumer.results()}")
        /* the output

    notBaggedItemsConsumer: [GroceryItem(name=Milk, weight=9.5)]

         */

        assertEquals(listOf(milk), notBaggedItemsConsumer.results())
    }

    private val MAX_WEIGHT = BigDecimal("8")

    private fun exceedsWeightLimit(intermediateConsumers: List<Consumer<GroceryItem>>, value: GroceryItem): Boolean {
        val bagWeight = intermediateConsumers[1]
        val weightInBag = (bagWeight.results() as BigDecimal)
        return weightInBag.add(value.weight) > MAX_WEIGHT
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

    data class GroceryItem(val name: String, val weight: BigDecimal)
}
