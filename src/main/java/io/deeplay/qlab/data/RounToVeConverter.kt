package io.deeplay.qlab.data

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import io.deeplay.qlab.parser.models.history.Round
import java.io.File
import java.util.*

fun main() {
    println("Парсинг...")
    val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        .run { RoundListFilter.filter(this) }
    println("Всего данных: ${history.size}")

    println("Генерация профилей...")
    val unitProfiles = genUnitProfiles(history)
    val locationProfiles = genLocationProfiles(history)
    val levels = history.map { it.locationLevel }.distinct().sorted()

    // Можно настроить выбор раундов
    val rounds = history.filter { it.ourUnits.size == 1 && it.opponentUnits.size == 1 }

    println("Юнитов в выбранных раундах: " +
            rounds.flatMap { it.ourUnits + it.opponentUnits }.map { it.name }.distinct().size
    )
    println(
        "Локаций в выбранных раундах: " +
                rounds.map { it.locationName }.distinct().size
    )

    println("Стандартизация данных...")
    var roundsStd = standardizeRounds(rounds, levels, unitProfiles, locationProfiles)

//    val normContext = genMinimaxNormContext(roundsStd)
//    println("Контекст нормализации:")
//    println(normContext.first.joinToString { String.format(Locale.ENGLISH, "%.8f", it) })
//    println(normContext.second.joinToString { String.format(Locale.ENGLISH, "%.8f", it) })

//    println("Нормализация данных...")
//    roundsStd = normalizeStd(roundsStd) { normContext }

    println("Сохранение в файл...")
    saveRounds(roundsStd, "trainData/11nnn.csv")
    println("Сохранено ${roundsStd.size} ${roundsStd.first().size}-мерных векторов")
}

private fun standardizeRounds(
    history: List<Round>,
    levels: List<Int>,
    unitProfiles: Profiles,
    locationProfiles: Profiles
): List<FloatArray> {

    val sizeDataOneUnit = // Длина профиля юнита + дополнительные данные (наш / не наш, source gold)
        unitProfiles.entries.first().value.size + 2

    val medianUnitProfile = transpose(unitProfiles.values)
        .map { col -> col.toList().median { it } }
    println("Медианный профиль юнита: $medianUnitProfile")

    val medianLocationProfile = transpose(locationProfiles.values)
        .map { col -> col.toList().median { it } }
    println("Медианный профиль локации: $medianLocationProfile")

    val maxPosition = history.maxOf { it.ourUnits.size + it.opponentUnits.size }
    val roundsStandardized = mutableListOf<FloatArray>()

    history.forEach { round ->
        val curRoundNorm = mutableListOf<Float>()
        // Профиль локации
        curRoundNorm.addAll(
            locationProfiles[round.locationName]
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
                        val rawProfile = unitProfiles[unit.name]
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

        roundsStandardized.add(curRoundNorm.toFloatArray())
    }
    return roundsStandardized
}

private fun normalize(
    data: List<FloatArray>,
    normContext: (List<FloatArray>) -> Pair<FloatArray, FloatArray> = ::genZNormContext
): List<FloatArray> {
    return data.map { it.applyNormByRow(normContext(data)).toFloatArray() }
}

private fun transpose(src: Collection<FloatArray>) = buildList {
    for (nCol in src.first().indices) {
        val column = src.map { it[nCol] }
        add(column.toFloatArray())
    }
}

private fun saveRounds(rounds: List<FloatArray>, fileName: String) {
    File(fileName)
        .also { it.parentFile.mkdirs() }
        .bufferedWriter().use { writer ->
            /*repeat(rounds.first().size - 1) {
                writer.write("p${it},")
            }*/
            // Для 1 на 1 будет такой порядок:
            writer.write("evl,agl,ral,shl,sgl,gpl,vrl,lvl1,lvl2,lvl3,lvl4,lvl5,lvl6,lvl7,lvl8,lvl9,lvl10,ev1,ag1,ra1,sh1,sg1,gp1,vr1,sgc1,our1,ev2,ag2,ra2,sh2,sg2,gp2,vr2,sgc2,our2,")
            writer.write("our_gp")
            writer.newLine()

            rounds.forEach { row ->
                writer.write(row.joinToString(",") {
                    String.format(Locale.ENGLISH, "%.8f", it)
                })
                writer.newLine()
            }
        }
}
