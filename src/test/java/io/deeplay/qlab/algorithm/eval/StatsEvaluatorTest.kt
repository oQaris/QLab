package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.input.EnemyLocation
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.abs

internal class StatsEvaluatorTest {

    @Test
    fun evaluateGoldProfit1Test() {
        val dataFile = File("testData/anonymized_data.json")

        assumeTrue(dataFile.exists(), "Data file not found")

        val history: List<Round> = Parser.parseRoundList(dataFile)
        val evaluator = StatsEvaluator(history)

        // данные из раунда 5c31e7d2-1884-47b6-9c62-a53ee9d4ad63
        val location = EnemyLocation("Factoria11", 50, 9, listOf(Unit("РЕФРОД ГАСТИНРИЦА", 101.0, 0)))
        val ourUnits = mutableSetOf(UnitWithLocation("ВИНРЕД ДООМАИКА", 101.3, 1, location))

        assertEquals(1.0, evaluator.evaluateGoldProfit(ourUnits))
    }

    @Test
    @Disabled
    fun meanErrorDemo() {
        val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        val testData = Parser.parseRoundList(File("testData/filtered1to1.json"))

        var error = 0.0
        testData.subList(10650, testData.size).forEach { round ->
            val evaluator = StatsEvaluator(history - round)

            val actual = round.ourUnits.sumOf { it.goldProfit }
            val expected = evaluator.evaluateGoldProfit(round.toUnitsWithLocation())

            val curError = abs(actual - expected) * 100 / (abs(actual) + 0.1)
            println(
                String.format("%.3f", actual) + "\t" +
                        String.format("%.3f", expected) + "\t" +
                        String.format("%.2f", curError)
            )
            error += curError / testData.size
        }
        println("Средняя погрешность = $error")
    }

    @Test
    @Disabled
    fun eqRoundsDemo() {
        val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        println("all: ${history.size}")
        val clearProfitRounds = history.toSet()
        println("distinct: ${clearProfitRounds.size}")
        clearProfitRounds.forEach { round ->
            round.ourUnits.forEach {
                it.aggression = 0
            }
            round.opponentUnits.forEach {
                it.aggression = 0
            }
        }
        println("without gold profit: ${clearProfitRounds.toSet().size}")
    }

    private fun Round.toUnitsWithLocation() = buildSet {
        val location = EnemyLocation(locationName, locationLevel, maxPositionsQuantity, opponentUnits as List<Unit>?)
        ourUnits.forEach { unit ->
            add(UnitWithLocation(unit.name, unit.sourceGoldCount, unit.locatePosition, location))
        }
    }.toMutableSet()


    @Test
    @Disabled
    fun stackDemo() {
        val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        history.forEach { round ->
            val allUnits = round.opponentUnits + round.ourUnits
            val maxPos = allUnits.maxOf { it.locatePosition }
            if (maxPos != allUnits.size - 1)
                println(round.roundId)
        }
    }
}
