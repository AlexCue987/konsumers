package com.tgt.trans.common.konsumers

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

val finn = Dog("Finn", "ACD", BigDecimal("1.2"), 43)
val leo = Dog("Leo", "ACD", BigDecimal("2.2"), 49)
val echo = Dog("Echo", "ACD", BigDecimal("3.3"), 44)
val bandit = Dog("Bandit", "GSD", BigDecimal("9"), 27)
val harvey = Dog("Harvey", "GSD", BigDecimal("2.5"), 82)
val chewie = Dog("Chewie", "GSD", BigDecimal("3"), 58)

val workingDogs = listOf(
        finn,
        leo,
        echo,
        bandit,
        harvey,
        chewie
)

val dogs = listOf(
        Dog("Max", "ACD", BigDecimal("1.2"), 43),
        Dog("Bandit", "Mutt", BigDecimal("9"), 27),
        Dog("Harley", "GSD", BigDecimal("3.5"), 82),
        Dog("Chewie", "Poodle", BigDecimal("3"), 58)
)

data class Dog(val name: String, val breed: String, val age: BigDecimal, val weight: Int)

data class Thing(val name: String, val quantity: Int)

data class WeatherReading(val takenAt: LocalDateTime, val degrees: BigDecimal)

data class WeatherByDay(val takenAt: LocalDate, val low: BigDecimal, val high: BigDecimal)

fun t() {
    val a = listOf(1, 2, 3).asSequence().map { it *2 }
        .filter { it > 0 }
        .toList()
}
