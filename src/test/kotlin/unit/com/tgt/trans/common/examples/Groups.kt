package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.consumers.*
import com.tgt.trans.common.aggregator2.decorators.allOf
import com.tgt.trans.common.aggregator2.decorators.groupBy
import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test

class Groups {
    @Test
    fun `Statistics by weather type`() {
        val actual = dailyWeather.consume(
            groupBy { it: DailyWeather -> it.weatherType }.count()
        )
    }

    @Test
    fun `compute aggregates for groups`() {
        val actual = (1..10).asSequence()
                .consume(
                         groupBy { a: Int -> a % 3 }.allOf(asList(), min(), max())
                    )

        assertEquals(
                listOf(
                        mapOf(0 to listOf(listOf(3, 6, 9), Optional.of(3), Optional.of(9)),
                            1 to listOf(listOf(1, 4, 7, 10), Optional.of(1), Optional.of(10)),
                            2 to listOf(listOf(2, 5, 8), Optional.of(2), Optional.of(8)))),
                actual)
    }

    val redTruck = Toy("Truck", "Red", 4)
    val blueBall = Toy("Ball", "Blue", 3)
    val blueCar = Toy("Car", "Blue", 2)
    val redFish = Toy("Fish", "Red", 1)

    @Test
    fun `groups objects by color`() {
        val toys = listOf(redTruck, blueBall, blueCar, redFish)
        val toysByColor = toys.consume(groupBy<Toy, String> { it.color }.asList())[0]
        val expected = mapOf("Red" to listOf(redTruck, redFish), "Blue" to listOf(blueBall, blueCar))
        assertEquals(expected, toysByColor as Map<String, List<Toy>>)
    }

    @Test
    fun `groups object fields by color`() {
        val toys = listOf(redTruck, blueBall, blueCar, redFish)
        val toysByColor = toys.consume(
            groupBy<Toy, String> { it.color }
                .mapTo { it:Toy -> it.name }
            .asList())[0]
        val expected = mapOf("Red" to listOf(redTruck.name, redFish.name),
            "Blue" to listOf(blueBall.name, blueCar.name))
        assertEquals(expected, toysByColor as Map<String, List<String>>)
    }

    @Test
    fun `groups and maps object fields by color`() {
        val toys = listOf(redTruck, blueBall, blueCar, redFish)
        val toysByColor = toys.consume(
            groupBy<Toy, String> { it.color }
                .mapTo { it:Toy -> it.weight }
                .asList())[0]
        val expected = mapOf("Red" to listOf(redTruck.weight, redFish.weight),
            "Blue" to listOf(blueBall.weight, blueCar.weight))
        assertEquals(expected, toysByColor as Map<String, List<Int>>)
    }
}

data class Toy(val name: String, val color: String, val weight: Int)
