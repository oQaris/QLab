package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.data.Standardizer
import io.deeplay.qlab.data.splitRounds
import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.input.EnemyLocation
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import krangl.mean
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import java.io.File


@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StatsEvaluatorTest {
    private val eps = 1E-6
    private val standardizer: Standardizer
    private val trainData: List<Round>
    private val testData: List<Round>
    private val trainDataStd: List<FloatArray>


    init {
        val data = splitRounds(
            Parser.parseRoundList(File("testData/anonymized_data.json"))
                .run { RoundListFilter.filter(this) },
            .2
        )

        trainData = data.first
        standardizer = Standardizer.fit(trainData)
        testData = data.second
        trainDataStd = standardizer.transformAll(trainData)
    }


    @Test
    fun evaluateGoldProfit_FromTrain1Test() {
        val evaluator = StatsEvaluator(standardizer, trainDataStd)

        // данные из раунда 5c31e7d2-1884-47b6-9c62-a53ee9d4ad63
        val location = EnemyLocation("Factoria11", 50, 9, listOf(Unit("РЕФРОД ГАСТИНРИЦА", 101.0, 0)))
        val ourUnits = mutableSetOf(UnitWithLocation("ВИНРЕД ДООМАИКА", 101.3, 1, location))

        assertEquals(1.0, evaluator.evaluateGoldProfit(ourUnits))
    }


    @Test
    fun evaluateGoldProfit_FromTest1Test() {
        val evaluator = StatsEvaluator(standardizer, trainDataStd)
        val round = testData[0]

        val location = EnemyLocation(
            round.locationName,
            round.locationLevel,
            round.maxPositionsQuantity,
            round.opponentUnits.map { Unit(it.name, it.sourceGoldCount, it.locatePosition) }
        )
        val ourUnits =
            round.ourUnits.map { UnitWithLocation(it.name, it.sourceGoldCount, it.locatePosition, location) }.toSet()

        assertEquals(round.ourUnits.sumOf { it.goldProfit }, evaluator.evaluateGoldProfit(ourUnits), eps)
    }


    @Test
    fun evaluateGoldProfit_FromTestMeanTest() {
        val evaluator = StatsEvaluator(standardizer, trainDataStd)

        val ourGoldProfits = testData.map { round -> round.ourUnits.sumOf { it.goldProfit } }
        val ourUnitLists = testData.map { round ->
            val location = EnemyLocation(
                round.locationName,
                round.locationLevel,
                round.maxPositionsQuantity,
                round.opponentUnits.map { Unit(it.name, it.sourceGoldCount, it.locatePosition) }
            )

            round.ourUnits.map { UnitWithLocation(it.name, it.sourceGoldCount, it.locatePosition, location) }.toSet()
        }
        val answers = ourUnitLists.map { evaluator.evaluateGoldProfit(it) }
        val errors = ourGoldProfits.mapIndexed { idx, gp -> gp - answers[idx] }

        assertEquals(0.0, errors.mean(), eps)
    }
}
