package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.data.r2Score
import io.deeplay.qlab.data.splitData
import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.input.EnemyLocation
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import krangl.mean
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import java.io.File
import kotlin.math.abs

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StatsEvaluatorTest {
    private val eps = 1E-6
    private val trainData: List<Round>
    private val testData: List<Round>
    private val evaluator: StatsEvaluator

    init {
        val data = splitData(
            Parser.parseRoundList(File("testData/anonymized_data.json"))
                .run { filter { it.ourUnits.size == 1 && it.opponentUnits.size == 1 } }
                .run { RoundListFilter.filter(this) },
            0.005
        )
        trainData = data.first
        testData = data.second
        evaluator = StatsEvaluator(trainData)
        println("Train dataset size: " + trainData.size)
        println("Test dataset size: " + testData.size)
    }

    @Test
    fun evaluateGoldProfit_FromTrain1Test() {
        val round = trainData.first()
        val ourUnits = round.toSetUnitWithLocation()

        assertEquals(round.ourUnits.sumOf { it.goldProfit }, evaluator.evaluateGoldProfit(ourUnits), eps)
    }

    @Test
    fun evaluateGoldProfit_FromTestMeanDemo() {
        val ourGoldProfits = testData.map { round -> round.ourUnits.sumOf { it.goldProfit } }
        val ourUnitLists = testData.map { it.toSetUnitWithLocation() }
        val answers = ourUnitLists.map { evaluator.evaluateGoldProfit(it) }
        val errors = ourGoldProfits.mapIndexed { idx, gp -> abs(gp - answers[idx]) }

        println("Средняя ошибка: ${errors.mean()}")
        println("Максимальная ошибка: ${errors.maxOf { it }}")
        println("R2 score: ${r2Score(ourGoldProfits, answers)}")
    }

    private fun Round.toSetUnitWithLocation(): Set<UnitWithLocation> {
        val location = EnemyLocation(
            locationName,
            locationLevel,
            maxPositionsQuantity,
            opponentUnits.map { Unit(it.name, it.sourceGoldCount, it.locatePosition) }
        )
        return ourUnits.map { UnitWithLocation(it.name, it.sourceGoldCount, it.locatePosition, location) }.toSet()
    }
}
