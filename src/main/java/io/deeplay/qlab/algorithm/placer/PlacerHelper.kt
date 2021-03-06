package io.deeplay.qlab.algorithm.placer

import com.github.shiguruikai.combinatoricskt.combinations
import com.github.shiguruikai.combinatoricskt.permutations
import com.github.shiguruikai.combinatoricskt.powerset
import io.deeplay.qlab.algorithm.eval.IEvaluator
import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.input.EnemyLocation
import io.deeplay.qlab.parser.models.output.UnitWithLocation

fun findBestArrangement(
    units: Set<Unit>,
    locations: Set<EnemyLocation>,
    strategy: IEvaluator
): Set<UnitWithLocation> {

    if (locations.isEmpty() || units.isEmpty())
        return emptySet()

    return locations.flatMap {
        arrangeUnitsOnLocation(units, it)

    }.maxByOrNull {
        strategy.evaluateGoldProfit(it.toSet())
    }!!.toSet()
}

private fun arrangeUnitsOnLocation(
    units: Set<Unit>,
    location: EnemyLocation
): Sequence<List<UnitWithLocation>> {

    val enemyPos = location.opponentUnits
        .map { it.locatePosition }.toSet()

    return units.powerset()
        .filter { it.size <= location.maxPositionsQuantity - enemyPos.size }
        .flatMap { unitSet ->

            val places = (0 until location.maxPositionsQuantity)
                .minus(enemyPos)
                .take(unitSet.size) // Убрать, если не нужна "стекоподобность" размещения

            generateUnitPositionPairs(unitSet, places).map { unitsWithPos ->
                unitsWithPos.map {
                    it.first.toUnitWithLocation(
                        location, it.second
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

private fun Unit.toUnitWithLocation(location: EnemyLocation, position: Int) =
    UnitWithLocation(name, sourceGoldCount, position, location)
