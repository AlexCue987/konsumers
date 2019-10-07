package com.tgt.trans.common.examples.transformations

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.transformations.peek
import com.tgt.trans.common.konsumers.transformations.transformTo
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformationExample {
    @Test
    fun `transformTo example`() {
        val shoppingList = listOf(
            ShoppingListItem("Apple", 2),
            ShoppingListItem("Orange", 1)
        )

        val actual = shoppingList.consume(
            peek<ShoppingListItem> { println("Processing $it") }
                .transformTo { item: ShoppingListItem ->
                    (1..item.quantity).asSequence().map { item.name } }
                .peek { println("Unpacked to $it") }
                .asList()
        )

        assertEquals(listOf("Apple", "Apple", "Orange"), actual[0])
    }

    data class ShoppingListItem(val name: String, val quantity: Int)
}
