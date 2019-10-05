package com.tgt.trans.common.examples.advanced

import com.tgt.trans.common.konsumers.consumers.*
import com.tgt.trans.common.konsumers.dispatchers.groupBy
import com.tgt.trans.common.konsumers.transformations.*
import java.time.Duration
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class RaceResults {
    data class Finisher(val name: String, val time: LocalTime, val ageGroup: String)

    val ageGroup1 = "10-99"
    val ageGroup2 = "100-999"
    val yoda = Finisher("Yoda", LocalTime.of(0, 25), ageGroup2)
    val luke = Finisher("Luke", LocalTime.of(0, 26), ageGroup1)
    val r2d2 = Finisher("R2D2", LocalTime.of(0, 27), ageGroup1)
    val chhewbacca = Finisher("Chewbacca", LocalTime.of(0, 28), ageGroup2)

    private val finishers = listOf(yoda, luke, r2d2, chhewbacca)

    data class RaceResult(val finisher: Finisher, val overallPlace: Int, val ageGroupPlace: Int, val minutesAfterWinnerFinished: Int)

    @Test
    fun `uses states to store overall and age group place and best time`() {
        val overallPlace = count<Finisher>()
        val bestTime = FirstN<Finisher>(1)
        val raceResults = finishers.consume(
            keepStates(overallPlace, bestTime)
                .groupBy(keyFactory = { finisher: Finisher -> finisher.ageGroup },
                    innerConsumerFactory = { ageGroupConsumer(overallPlace, bestTime) })
        )

        raceResults.forEach { println(it) }

        val expected = mapOf(
            ageGroup2 to listOf(
                RaceResult(yoda, overallPlace = 1, ageGroupPlace = 1, minutesAfterWinnerFinished = 0),
                RaceResult(chhewbacca, overallPlace = 4, ageGroupPlace = 2, minutesAfterWinnerFinished = 3)
            ),
            ageGroup1 to listOf(
                RaceResult(luke, overallPlace = 2, ageGroupPlace = 1, minutesAfterWinnerFinished = 1),
                RaceResult(r2d2, overallPlace = 3, ageGroupPlace = 2, minutesAfterWinnerFinished = 2)
            )
        )

        assertEquals(expected, raceResults[0])
    }

    private fun ageGroupConsumer(overallPlace: Counter<Finisher>, bestTime: FirstN<Finisher>): Consumer<Finisher> {
        val ageGroupPlace = count<Finisher>()
        return keepState(ageGroupPlace).mapTo { it: Finisher -> RaceResult(it,
            overallPlace=overallPlace.counter.toInt(),
            ageGroupPlace = ageGroupPlace.counter.toInt(),
            minutesAfterWinnerFinished = Duration.between(bestTime.buffer[0].time, it.time).toMinutes().toInt())}
            .asList()
    }
}
