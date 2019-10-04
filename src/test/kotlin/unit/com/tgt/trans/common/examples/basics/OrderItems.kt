package com.tgt.trans.common.examples.basics

import kotlin.test.Test

class OrderItems {
    data class OrderItem(val name: String, val quantity: Int)

    private val orderItems = listOf(
        OrderItem("Apple", 2),
        OrderItem("Orange", 3))

//    @Test
//    fun `transform in two steps, using flatMap`() {
//        val items = orderItems.
//    }
//
//    fun expandList
}
