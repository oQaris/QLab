package io.deeplay.qlab.data

import io.deeplay.qlab.parser.models.history.Round
import io.deeplay.qlab.parser.models.output.UnitWithLocation

class Standardizer(
    var maxPosition: Int,
    val levels: List<Int>,
    val unitsProfiles: Profiles,
    val locationsProfiles: Profiles
) {
    val sizeDataOneUnit = // Длина профиля юнита + дополнительные данные
        unitsProfiles.entries.first().value.size + 2

    val medianUnitProfile = genMedianProfile(unitsProfiles)
    val medianLocationProfile = genMedianProfile(locationsProfiles)

    companion object {

        fun fit(history: List<Round>): Standardizer {
            val maxPosition = history.maxOf { it.ourUnits.size + it.opponentUnits.size }
            val levels = history.map { it.locationLevel }.distinct().sorted()
            val unitsProfiles = genUnitProfiles(history)
            val locationsProfiles = genLocationProfiles(history)

            return Standardizer(maxPosition, levels, unitsProfiles, locationsProfiles)
        }
    }

    fun transformAll(rounds: List<Round>, trimMaxPos: Boolean = false): List<FloatArray> {
        //TODO Костыль наверное
        val oldMaxPos = maxPosition
        if (trimMaxPos)
            maxPosition = rounds.maxOf { it.ourUnits.size + it.opponentUnits.size }

        return rounds.map { round ->
            transform(round)
        }.also {
            maxPosition = oldMaxPos
        }
    }

    fun transform(units: List<UnitWithLocation>): FloatArray {
        TODO("Доделать")
    }

    fun transform(round: Round): FloatArray {
        val curRoundNorm = mutableListOf<Float>()
        // Профиль локации
        curRoundNorm.addAll(
            locationsProfiles[round.locationName]
                ?.toList() ?: medianLocationProfile
        )
        // One-Hot encoding уровня
        curRoundNorm.addAll(
            FloatArray(levels.size)
            { if (levels[it] == round.locationLevel) 1f else 0f }.toList()
        )
        // Данные о расположенных юнитах
        val roundProfiles = (round.ourUnits + round.opponentUnits).let { units ->
            buildList {
                repeat(maxPosition) { pos ->
                    val unit = units.firstOrNull { it.locatePosition == pos }
                    if (unit != null) {
                        val rawProfile = unitsProfiles[unit.name]
                            ?.toList() ?: medianUnitProfile
                        add(
                            rawProfile // добавляем в конец sourceGold в раунде
                                .plus((unit.sourceGoldCount).toFloat())
                                .plus( // 1 - наш, 0 - не наш
                                    if (unit in round.ourUnits) listOf(1f)
                                    else listOf(0f)
                                ).also { require(it.size == sizeDataOneUnit) }
                        )
                    } else add(FloatArray(sizeDataOneUnit).toList()) // нули
                }
            }
        }.flatten()
        curRoundNorm.addAll(roundProfiles)
        // Голд профит
        curRoundNorm.add((round.ourUnits.sumOf { it.goldProfit }).toFloat())
        return curRoundNorm.toFloatArray()
    }

    private fun transpose(src: Collection<FloatArray>) = buildList {
        for (nCol in src.first().indices) {
            val column = src.map { it[nCol] }
            add(column.toFloatArray())
        }
    }

    private fun genMedianProfile(profiles: Profiles) =
        transpose(profiles.values)
            .map { col -> col.toList().median { it } }
}
