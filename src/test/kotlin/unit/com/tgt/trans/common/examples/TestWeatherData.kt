package com.tgt.trans.common.examples

import java.math.BigDecimal
import java.time.LocalDate

enum class Weather { Sunny, Cloudy }

data class DailyWeather(val day: LocalDate, val weather: Weather, val rainAmount: BigDecimal, val high: Int, val low: Int)

val noRain = BigDecimal.ZERO
val drizzle = BigDecimal("0.1")
val someRain = BigDecimal("0.2")
val bigRain = BigDecimal.ONE

val dailyWeather = listOf(
        DailyWeather(LocalDate.of(2019, 4, 1), Weather.Sunny, noRain, 30, 20),
        DailyWeather(LocalDate.of(2019, 4, 2), Weather.Cloudy, someRain, 54, 44),
        DailyWeather(LocalDate.of(2019, 4, 3), Weather.Cloudy, drizzle, 44, 35),
        DailyWeather(LocalDate.of(2019, 4, 4), Weather.Cloudy, drizzle, 30, 20),
        DailyWeather(LocalDate.of(2019, 4, 5), Weather.Sunny, noRain, 75, 54),
        DailyWeather(LocalDate.of(2019, 4, 6), Weather.Cloudy, noRain, 31, 21),
        DailyWeather(LocalDate.of(2019, 4, 7), Weather.Sunny, noRain, 38, 29),
        DailyWeather(LocalDate.of(2019, 4, 8), Weather.Cloudy, bigRain, 60, 41),
        DailyWeather(LocalDate.of(2019, 4, 9), Weather.Sunny, noRain, 55, 20),
        DailyWeather(LocalDate.of(2019, 4, 10), Weather.Sunny, noRain, 59, 31),
        DailyWeather(LocalDate.of(2019, 4, 11), Weather.Cloudy, drizzle, 30, 20),
        DailyWeather(LocalDate.of(2019, 4, 12), Weather.Cloudy, bigRain, 64, 37)
)