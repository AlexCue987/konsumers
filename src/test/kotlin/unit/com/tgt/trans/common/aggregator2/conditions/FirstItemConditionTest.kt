package com.tgt.trans.common.aggregator2.conditions

import com.tgt.trans.common.aggregator2.WeatherReading
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstItemConditionTest {
    val sut = sameProjectionAsFirst<WeatherReading, LocalDate>
            { a: WeatherReading -> a.takenAt.toLocalDate() }

    val mondayMorningWeather = WeatherReading(LocalDateTime.of(2019, 7, 15, 7, 6), BigDecimal(55))
    val mondayNightWeather = WeatherReading(LocalDateTime.of(2019, 7, 15, 18, 6), BigDecimal(55))
    val tuesdayMorningWeather = WeatherReading(LocalDateTime.of(2019, 7, 16, 7, 6), BigDecimal(55))

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
        sut[mondayMorningWeather]
        assertFalse(sut[tuesdayMorningWeather])
    }

    @Test
    fun `forgets item that did not match`() {
        sut[mondayNightWeather]
        assertFalse(sut[tuesdayMorningWeather], "Guardian assumption: last item did not match")
        assertTrue { sut[mondayMorningWeather] }
    }

    @Test
    fun `provides empty copy`() {
        sut[mondayMorningWeather]
        val emptyCopy = sut.emptyCopy()
        assertAll(
            { assertFalse { sut[tuesdayMorningWeather] } },
            { assertTrue { emptyCopy[tuesdayMorningWeather] } }
        )
    }
}
