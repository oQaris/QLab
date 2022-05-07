package io.deeplay.qlab.algorithm.eval

import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.output.UnitWithLocation

class MonteCarloEvaluator : IEvaluator {
    //todo заполнение
    val history: List<Round> = emptyList()
    val stats: Map<String, Stat> = emptyMap()
    val medianStat = Stat(0.5, 0.5, 0.5, 0.5)
    var eps = 1.0

    override fun evaluateGoldProfit(units: MutableSet<UnitWithLocation>): Double {

        return units.groupBy { it.location }
            .entries.sumOf { (location, units) ->

                val targetEnemyStats = location.opponentUnits.toStats()
                val targetOurStats = units.toStats()

                val similarRounds = history.filter { round ->
                    //todo добавить поэтапное уменьшение eps
                    equalsStats(round.opponentUnits.toStats(), targetEnemyStats)
                            && equalsStats(round.ourUnits.toStats(), targetOurStats)
                }

                // средний голд профит из похожих раундов
                similarRounds.sumOf { r ->
                    r.ourUnits.sumOf { it.goldProfit }
                } / similarRounds.size
            }
    }

    private fun equalsStats(historyStats: List<Stat>, targetStats: List<Stat>): Boolean {
        if (historyStats.size != targetStats.size) return false

        return historyStats.zip(targetStats)
            .all { (historyStat, targetStat) ->
                historyStat.equalsWithEps(targetStat, eps)
                //todo добавить комбинаторики
            }
    }

    private fun Iterable<Unit>.toStats() = map { stats[it.name] ?: medianStat }
}

data class Stat(val evas_prop: Double, val aggr_prop: Double, val resp_prop: Double, val shie_prop: Double) {

    fun equalsWithEps(other: Stat, eps: Double): Boolean {
        //todo реализовать
        return false
    }
}
