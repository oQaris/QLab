package io.deeplay.qlab.algorithm.eval

import com.github.shiguruikai.combinatoricskt.permutations
import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.input.EnemyLocation
import io.deeplay.qlab.parser.models.output.UnitWithLocation
import kotlin.math.abs

class MonteCarloEvaluator(private val history: List<Round>) : IEvaluator {

    private val stats: Map<String, Stat> = history.flatMap { it.ourUnits + it.opponentUnits }
        .groupBy { it.name }.mapValues { (_, units) ->
            Stat(
                units.sumOf { it.evasiveness }.toDouble() / units.size,
                units.sumOf { it.aggression }.toDouble() / units.size,
                units.sumOf { it.responseAggression }.toDouble() / units.size,
                units.sumOf { it.shield }.toDouble() / units.size,
            )
        }

    private val medianStat = Stat(0.34, 0.169, 0.026, 0.021)


    override fun evaluateGoldProfit(units: MutableSet<UnitWithLocation>): Double {

        return units.groupBy { it.location }
            .entries.sumOf { (location, units) ->

                val similarRounds = searchSimilarRounds(location, units)

                // медианный голд профит из похожих раундов
                similarRounds.map { r ->
                    r.ourUnits.sumOf { it.goldProfit }
                }.run { get(size / 2) }
            }
    }

    private fun searchSimilarRounds(location: EnemyLocation, units: List<UnitWithLocation>): List<Round> {
        val targetEnemyStats = location.opponentUnits.toStats()
        val targetOurStats = units.toStats()

        var eps = 1.0
        var similarRounds = history

        while (true) {
            val curRounds = similarRounds.filter { round ->
                //todo сделать не такой критичный отсев
                round.locationName == location.locationName
                        && round.locationLevel == location.locationLevel
                        && round.opponentUnits.positions() == location.opponentUnits.positions()
                        && units.positions() == round.ourUnits.positions()
                        && equalsStats(round.opponentUnits.toStats(), targetEnemyStats, eps)
                        && equalsStats(round.ourUnits.toStats(), targetOurStats, eps)
            }
            if (curRounds.isEmpty() || eps < 1E-10)
                return similarRounds

            similarRounds = curRounds
            eps /= 2
        }
    }

    private fun equalsStats(historyStats: List<Stat>, targetStats: List<Stat>, eps: Double): Boolean {
        if (historyStats.size != targetStats.size) return false

        return historyStats.permutations().any {
            it.zip(targetStats)
                .all { (historyStat, targetStat) ->
                    historyStat.equalsWithEps(targetStat, eps)
                }
        }
    }

    private fun Iterable<Unit>.positions() = map { it.locatePosition }.toSet()

    private fun Iterable<Unit>.toStats() = map { stats[it.name] ?: medianStat }
}

data class Stat(val evas_prop: Double, val aggr_prop: Double, val resp_prop: Double, val shie_prop: Double) {

    fun equalsWithEps(other: Stat, eps: Double): Boolean {
        return abs(evas_prop - other.evas_prop) < eps
                && abs(aggr_prop - other.aggr_prop) < eps
                && abs(resp_prop - other.resp_prop) < eps
                && abs(shie_prop - other.shie_prop) < eps
    }
}
