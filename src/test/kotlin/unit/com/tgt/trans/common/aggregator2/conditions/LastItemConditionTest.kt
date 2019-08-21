package com.tgt.trans.common.aggregator2.conditions

import com.tgt.trans.common.aggregator2.WeatherReading
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import org.junit.jupiter.api.assertAll
import java.lang.Math.abs
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LastItemConditionTest {
    val sut = lastItemCondition<WeatherReading>({ a: WeatherReading, b: WeatherReading ->
        abs(a.degrees.toDouble() - b.degrees.toDouble()) < 30
    })

    val mondayMorningWeather = WeatherReading(LocalDateTime.of(2019, 7, 15, 7, 6), BigDecimal(55))
    val mondayNightWeather = WeatherReading(LocalDateTime.of(2019, 7, 15, 18, 6), BigDecimal(75))
    val tuesdayMorningWeather = WeatherReading(LocalDateTime.of(2019, 7, 16, 7, 6), BigDecimal(33))
    val tuesdayNightWeather = WeatherReading(LocalDateTime.of(2019, 7, 16, 19, 1), BigDecimal(69))

    @Test
    fun `true for first item`() {
        assertTrue(sut[mondayMorningWeather])
    }

    @Test
    fun `true for second item`() {
        sut[mondayMorningWeather]
        assertTrue(sut[mondayNightWeather])
    }

    @Test
    fun `false for second item`() {
        sut[mondayNightWeather]
        assertFalse(sut[tuesdayMorningWeather])
    }

    @Test
    fun `forgets item that did not match`() {
        sut[mondayNightWeather]
        assertFalse(sut[tuesdayMorningWeather], "Guardian assumption: last item did not match")
        assertTrue { sut[tuesdayNightWeather] }
    }

    @Test
    fun `provides empty copy`() {
        sut[mondayNightWeather]
        val emptyCopy = sut.emptyCopy()
        assertAll(
            { assertFalse { sut[tuesdayMorningWeather] } },
            { assertTrue { emptyCopy[tuesdayMorningWeather] } }
        )
    }

    @Test
    fun `increasing and decreasing`() {
        val actual = listOf(1, 2, 0, 0, 2, -1, 3).consume(
            increasing<Int>().asList(),
            nonIncreasing<Int>().asList(),
            decreasing<Int>().asList(),
            nonDecreasing<Int>().asList()
        )
        assertAll(
            { assertEquals(listOf(1, 2, 3), actual[0], "increasing") },
            { assertEquals(listOf(1, 0, 0, -1), actual[1], "nonIncreasing") },
            { assertEquals(listOf(1, 0, -1), actual[2], "decreasing") },
            { assertEquals(listOf(1, 2, 2, 3), actual[3], "nonDecreasing") }
        )
    }
}
