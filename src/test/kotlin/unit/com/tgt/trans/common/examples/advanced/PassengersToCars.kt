package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.Consumer

class Bagger(private val innerConsumer: Consumer<Bag>,
             private val maxItems: Int,
             private val maxWeight: Int): Consumer<GroceryItem> {
    private var itemsCount = 0
    private var itemsWeight = 0
    private val buffer = Array<GroceryItem?>(maxItems) { null }

    override fun process(value: GroceryItem) {
        if (itemWouldNotFit(value)) {
            completeNewBag()
        }
        addItem(value)
    }

    override fun stop() {
        if (itemsCount > 0) {
            completeNewBag()
        }
    }

    private fun itemWouldNotFit(value: GroceryItem) =
        (itemsCount + 1) > maxItems || (itemsWeight + value.weight) > maxWeight

    private fun addItem(value: GroceryItem) {
        buffer[itemsCount++] = value
        itemsWeight += value.weight
    }

    private fun completeNewBag() {
        innerConsumer.process(Bag(currentItems()))
        itemsCount = 0
        itemsWeight = 0
    }

    private fun currentItems() = (0 until itemsCount).asSequence().map { buffer[it]!! }.toList()

    override fun results() = innerConsumer.results()
}

data class GroceryItem(val name: String, val weight: Int)

data class Bag(val items: List<GroceryItem>)
