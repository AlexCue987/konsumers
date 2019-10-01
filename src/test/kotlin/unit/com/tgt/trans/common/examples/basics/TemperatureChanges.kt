package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.counter
import com.tgt.trans.common.aggregator2.decorators.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test

class TemperatureChanges {
    val monday = LocalDate.of(2019, 9, 23)
    val tuesday = LocalDate.of(2019, 9, 24)
    val morning = LocalTime.of(7, 15)
    val night = LocalTime.of(17, 20)

    private val temperatures = listOf(
        Temperature(monday.atTime(morning), 46),
        Temperature(monday.atTime(night), 58),
        Temperature(tuesday.atTime(morning), 44),
        Temperature(tuesday.atTime(night), 61)
    )

    data class Temperature(val takenAt: LocalDateTime, val temperature: Int)

    data class TemperatureChange(val takenAt: LocalDateTime, val temperature: Int, val change: Int)

    @Test
    fun `compute temperature changes`() {
        val lastTwoItems = last<Temperature>(2).asList()
        val changes = temperatures.consume(
            keepState(lastTwoItems)
                .skip(1)
                .peek { println("current item $it, last two items: ${lastTwoItems.results()}") }
                .mapTo { it -> val previousTemperature = (lastTwoItems.results() as List<Temperature>)[0].temperature
                    TemperatureChange(it.takenAt, it.temperature, it.temperature - previousTemperature)
                }.asList())

        print(changes)
    }
}
