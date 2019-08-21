package com.tgt.trans.common.examples2

import java.math.BigDecimal
import java.time.LocalDate

enum class WeatherType { Sunny, Cloudy }

data class DailyWeather(val day: LocalDate, val weatherType: WeatherType, val rainAmount: BigDecimal, val high: Int, val low: Int)

val noRain = BigDecimal.ZERO
val drizzle = BigDecimal("0.1")
val someRain = BigDecimal("0.2")
val bigRain = BigDecimal.ONE

val dailyWeather = listOf(
        DailyWeather(LocalDate.of(2019, 4, 1), WeatherType.Sunny, noRain, 30, 20),
        DailyWeather(LocalDate.of(2019, 4, 2), WeatherType.Cloudy, someRain, 54, 44),
        DailyWeather(LocalDate.of(2019, 4, 3), WeatherType.Cloudy, drizzle, 44, 35),
        DailyWeather(LocalDate.of(2019, 4, 4), WeatherType.Cloudy, drizzle, 30, 20),
        DailyWeather(LocalDate.of(2019, 4, 5), WeatherType.Sunny, noRain, 75, 54),
        DailyWeather(LocalDate.of(2019, 4, 6), WeatherType.Cloudy, noRain, 31, 21),
        DailyWeather(LocalDate.of(2019, 4, 7), WeatherType.Sunny, noRain, 38, 29),
        DailyWeather(LocalDate.of(2019, 4, 8), WeatherType.Cloudy, bigRain, 60, 41),
        DailyWeather(LocalDate.of(2019, 4, 9), WeatherType.Sunny, noRain, 55, 20),
        DailyWeather(LocalDate.of(2019, 4, 10), WeatherType.Sunny, noRain, 59, 31),
        DailyWeather(LocalDate.of(2019, 4, 11), WeatherType.Cloudy, drizzle, 30, 20),
        DailyWeather(LocalDate.of(2019, 4, 12), WeatherType.Cloudy, bigRain, 64, 37)
)
