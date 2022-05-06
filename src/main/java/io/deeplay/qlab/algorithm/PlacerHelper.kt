package io.deeplay.qlab.algorithm

import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations
import com.github.shiguruikai.combinatoricskt.powerset
import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.input.EnemyLocation
import io.deeplay.qlab.parser.models.output.UnitWithLocation

var counter = 0

fun findBestArrangement(units: Set<Unit>, locations: Set<EnemyLocation>, strategy: IEvaluator): Set<UnitWithLocation> {
    if (locations.isEmpty() || units.isEmpty())
        return emptySet()

    return locations.flatMap {
        arrangeUnitsOnLocation(units, it)
    }.maxByOrNull {
        counter++ // log
        strategy.evaluateGoldProfit(it.toSet())
    }!!.toSet().also {
        println(counter) // log
    }
}

private fun arrangeUnitsOnLocation(units: Set<Unit>, location: EnemyLocation): Sequence<List<UnitWithLocation>> {
    val enemyPos = location.opponentUnits.map { it.locatePosition }.toSet()
    return units.powerset()
        .filter { it.size <= location.maxPositionsQuantity - enemyPos.size }
        .flatMap { unitSet ->
            val places = (0 until location.maxPositionsQuantity).minus(enemyPos)
            generateUnitPositionPairs(unitSet, places).map { unitsWithPos ->
                unitsWithPos.map {
                    it.first.toUnitWithLocation(
                        location.locationName, it.second
                    )
                }
            }
        }
}

private fun generateUnitPositionPairs(
    units: Collection<Unit>,
    places: Collection<Int>
): Sequence<List<Pair<Unit, Int>>> {
    require(places.size >= units.size)
    return places.combinations(units.size)
        .flatMap { it.permutations() } // Убрать, если позиции не влияют на юнитов (т.е. 123 = 312)
        .map { units.zip(it) }
}

private fun Unit.toUnitWithLocation(locationName: String, position: Int) =
    UnitWithLocation(name, sourceGoldCount, position, locationName)
