package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.data.Normalizer
import io.deeplay.qlab.data.Standardizer
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import kotlin.math.pow

class StatsEvaluator(trainData: List<Round>) : IEvaluator {
    private val standardizer = Standardizer.fit(trainData)
    private val normalizer1: Normalizer
    private val normalizer2: Normalizer
    private val history: List<FloatArray>

    init {
        val stdRounds = standardizer.transformAll(trainData)
        val endCol = stdRounds.first().size - 1

        normalizer1 = Normalizer.fitRobust(stdRounds)
        val norm1Rounds = normalizer1.transformAll(stdRounds, endCol)

        normalizer2 = Normalizer.fitMinimax(norm1Rounds)
        history = normalizer2.transformAll(stdRounds, endCol)
    }

    override fun evaluateGoldProfit(units: Set<UnitWithLocation>): Double {
        return units.groupBy { it.location.locationName }
            .entries.sumOf { (_, units) ->
                val vec = standardizer.transform(units)
                val roundNorm = normalizer2.transform(normalizer1.transform(vec))
                predictGold(roundNorm).toDouble()
            }
    }

    private fun predictGold(roundStd: FloatArray): Float {
        return history.minByOrNull { historyRoundStd ->
            historyRoundStd.zip(roundStd)
                .sumOf { (x, xCur) ->
                    (x - xCur).toDouble().pow(2)
                }

        }!!.last()
    }
}
