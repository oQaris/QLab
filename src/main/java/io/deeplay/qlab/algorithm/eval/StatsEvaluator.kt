package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.data.Standardizer
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import kotlin.math.pow

class StatsEvaluator(val standardizer: Standardizer, val history: List<FloatArray>): IEvaluator {
    override fun evaluateGoldProfit(units: Set<UnitWithLocation>): Double {
        return units.groupBy { it.location.locationName }
            .entries.sumOf { (_, units) ->
                val roundStd = standardizer.transform(units)
                predictGold(roundStd).toDouble()
            }
    }

    private fun predictGold(roundStd: FloatArray): Float {
        return history.minByOrNull { historyRoundStd ->
            historyRoundStd.zip(roundStd)
                .sumOf { (x, xPred) ->
                    (x - xPred).toDouble().pow(2)
                }

        }!!.last()
    }
}
