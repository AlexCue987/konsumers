package com.tgt.trans.common.examples.basics

import com.tgt.trans.common.aggregator2.consumers.LastN
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TemperatureChanges {
    val monday = LocalDate.of(2019, 9, 23)
    val tuesday = LocalDate.of(2019, 9, 24)
    val morning = LocalTime.of(7, 15)
    val night = LocalTime.of(17, 20)

    val mondayNight = monday.atTime(night)
    val tuesdayMorning = tuesday.atTime(morning)
    val tuesdayNight = tuesday.atTime(night)

    private val temperatures = listOf(
        Temperature(monday.atTime(morning), 46),
        Temperature(mondayNight, 58),
        Temperature(tuesdayMorning, 44),
        Temperature(tuesdayNight, 61)
    )

    data class Temperature(val takenAt: LocalDateTime, val temperature: Int)

    data class TemperatureChange(val takenAt: LocalDateTime, val temperature: Int, val change: Int)

    @Test
    fun `compute temperature changes`() {
        val lastTwoItems = LastN<Temperature>(2)
        val changes = temperatures.consume(
            keepState(lastTwoItems)
                .peek { println("current item $it") }
                .skip(1)
                .peek { println("  last two items: ${lastTwoItems.results()}") }
                .mapTo { it ->
                    val previousTemperature = lastTwoItems.results().last().temperature
                    TemperatureChange(it.takenAt, it.temperature, it.temperature - previousTemperature)
                }
                .peek { println("  change: $it") }
                .asList())

        val expected = listOf(
            TemperatureChange(takenAt = mondayNight, temperature = 58, change = 12),
            TemperatureChange(takenAt = tuesdayMorning, temperature = 44, change = -14),
            TemperatureChange(takenAt = tuesdayNight, temperature = 61, change = 17)
        )

        assertEquals(expected, changes[0])
    }
}
