package io.deeplay.qlab.data

import io.deeplay.qlab.parser.models.UnitWithResult
import io.deeplay.qlab.parser.models.history.Round

typealias Profiles = Map<String, FloatArray>

const val minUnitEntries = 10
const val minLocationEntries = 3

fun genUnitProfiles(history: List<Round>): Profiles {
    val unitEntries = history.flatMap { it.ourUnits + it.opponentUnits }

    val unitsWithFreq = unitEntries
        .groupingBy { it.name }
        .eachCount()
    val deletedUnits = unitsWithFreq
        .filter { it.value < minUnitEntries }
        .map { it.key }

    println("${deletedUnits.size} юнитов из ${unitsWithFreq.size} должны быть заменены на медианный профиль")

    return unitEntries
        .filterNot { it.name in deletedUnits }
        .groupBy { it.name }
        .mapValues { (_, entries) ->
            entries.toProfile()
        }
}

fun genLocationProfiles(history: List<Round>): Profiles {
    val locsWithFreq = history
        .groupingBy { it.locationName }
        .eachCount()
    val deletedLocs = locsWithFreq
        .filter { it.value < minUnitEntries }
        .map { it.key }

    println("${deletedLocs.size} локаций из ${locsWithFreq.size} должны быть заменены на медианный профиль")

    return history
        .filterNot { it.locationName in deletedLocs }
        .groupBy { it.locationName }
        .mapValues { (_, entries) ->
            val unitEntries = entries.flatMap { it.ourUnits + it.opponentUnits }
            unitEntries.toProfile()
        }
}

private fun List<UnitWithResult>.toProfile() =
    listOf(
        mean { it.evasiveness.toFloat() },
        mean { it.aggression.toFloat() },
        mean { it.responseAggression.toFloat() },
        mean { it.shield.toFloat() },
        mean { it.sourceGoldCount.toFloat() },
        mean { it.goldProfit.toFloat() },
        mean { if (it.goldProfit > 0) 1f else 0f } // винрейт
    ).toFloatArray()
