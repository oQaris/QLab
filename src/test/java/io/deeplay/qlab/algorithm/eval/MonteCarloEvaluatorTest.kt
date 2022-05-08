package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.input.EnemyLocation
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.abs

internal class MonteCarloEvaluatorTest {

    @Test
    fun evaluateGoldProfit1Test() {
        val history: List<Round> = Parser.parseRoundList(File("testData/anonymized_data.json"))
        val evaluator = MonteCarloEvaluator(history)

        // данные из раунда 5c31e7d2-1884-47b6-9c62-a53ee9d4ad63
        val location = EnemyLocation("Factoria11", 50, 9, listOf(Unit("РЕФРОД ГАСТИНРИЦА", 101.0, 0)))
        val ourUnits = mutableSetOf(UnitWithLocation("ВИНРЕД ДООМАИКА", 101.3, 1, location))

        assertEquals(1.0, evaluator.evaluateGoldProfit(ourUnits))
    }

    @Test
    fun meanErrorDemo() {
        val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        val testData = Parser.parseRoundList(File("testData/filtered.json"))

        var error = 0.0
        testData.forEach { round ->
            val evaluator = MonteCarloEvaluator(history - round)

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

    private fun Round.toUnitsWithLocation() = buildSet {
        val location = EnemyLocation(locationName, locationLevel, maxPositionsQuantity, opponentUnits as List<Unit>?)
        ourUnits.forEach { unit ->
            add(UnitWithLocation(unit.name, unit.sourceGoldCount, unit.locatePosition, location))
        }
    }.toMutableSet()
}
