package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.data.Standardizer
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import kotlin.math.pow

class StatsEvaluator(history: List<Round>) : IEvaluator {
    private val stdr = Standardizer.fit(history)
    private val allRoundStd = stdr.transformAll(history)

    override fun evaluateGoldProfit(units: MutableSet<UnitWithLocation>): Double {

        return units.groupBy { it.location.locationName }
            .entries.sumOf { (_, units) ->
                val newRoundStd = stdr.transform(units)
                predictGold(newRoundStd).toDouble()
            }
    }

    private fun predictGold(roundStd: FloatArray): Float {
        return allRoundStd.minByOrNull { historyRoundStd ->
            historyRoundStd.zip(roundStd)
                .sumOf { (x, xPred) ->
                    (x - xPred).toDouble().pow(2)
                }

        }!!.last()
    }
}
