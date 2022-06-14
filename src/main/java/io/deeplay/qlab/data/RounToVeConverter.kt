package io.deeplay.qlab.data

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import io.deeplay.qlab.parser.models.UnitWithResult
import io.deeplay.qlab.parser.models.history.Round
import java.io.File
import java.util.*

fun main() {
    println("Парсинг...")
    val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        .run { RoundListFilter.filter(this) }
        //.run { filterRoundsWithFreqUnits(this) }

    println("Генерация профилей...")
    val profiles = genProfiles(history)
    val levels = history.map { it.locationLevel }.distinct().sorted()

    // Можно настроить выбор раундов
    val filter = listOf(1 to 1)

    filter.forEach { (our, opp) ->
        println("\nНаших: $our, Противников: $opp")
        val rounds = history.filter { it.ourUnits.size == our && it.opponentUnits.size == opp }

        println("Стандартизация данных...")
        var roundsStd = standardizeRounds(rounds, levels, profiles)

        val normContext = genZNormContext(roundsStd)
        println("Контекст нормализации:")
        println(normContext.first.joinToString { String.format(Locale.ENGLISH, "%.8f", it) })
        println(normContext.second.joinToString { String.format(Locale.ENGLISH, "%.8f", it) })

        println("Нормализация данных...")
        roundsStd = normalizeStd(roundsStd) { normContext }

        println("Сохранение в файл...")
        saveRounds(roundsStd, "trainData/11new.csv")
        println("Сохранено ${roundsStd.size} ${roundsStd.first().size}-мерных векторов")
    }
}

private fun filterRoundsWithFreqUnits(history: List<Round>): List<Round> {
    val unitAppearances = mutableMapOf<UnitWithResult, Int>()
    history.forEach { rnd ->
        (rnd.ourUnits + rnd.opponentUnits)
            .forEach {
                unitAppearances.merge(it, 1) { old, one -> old + one }
            }
    }
    val frequentUnits = unitAppearances.filter { it.value > 5 }.keys
    return history.filter { frequentUnits.containsAll(it.ourUnits + it.opponentUnits) }
}

private fun standardizeRounds(
    history: List<Round>,
    levels: List<Int>,
    profiles: Map<Pair<String, Int>, FloatArray>
): List<FloatArray> {

    val sizeDataOneUnit = // Длина профиля + дополнительные данные
        profiles.entries.first().value.size + 3

    val maxPosition = history.maxOf { it.ourUnits.size + it.opponentUnits.size }
    val medianProfile = transpose(profiles.values)
        .map { col -> col.toList().median { it } }
    println("Медианный профиль: $medianProfile")

    val roundsStandardized = mutableListOf<FloatArray>()
    history.forEach { round ->
        val curRoundNorm = mutableListOf<Float>()
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
                        val rawProfile = profiles[unit.name to unit.locatePosition]
                            ?.toList() ?: medianProfile
                        add(
                            rawProfile // добавляем в конец sourceGold в раунде
                                .plus((unit.sourceGoldCount).toFloat())
                                .plus( // 01 - наш, 10 - не наш
                                    if (unit in round.ourUnits) listOf(0f, 1f)
                                    else listOf(1f, 0f)
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

private fun normalizeStd(
    data: List<FloatArray>,
    normContext: (List<FloatArray>) -> Pair<FloatArray, FloatArray> = ::genZNormContext
): List<FloatArray> {
    return data.map { it.applyNormByRow(normContext(data)).toFloatArray() }
}

private fun genProfiles(history: List<Round>): Map<Pair<String, Int>, FloatArray> {
    val numRoundsNeeded = 100
    val unitsEntries = history.flatMap { it.ourUnits + it.opponentUnits }

    val unitsWithFreq = unitsEntries
        .groupingBy { it.name }
        .eachCount()
    val deletedUnits = unitsWithFreq
        .filter { it.value < numRoundsNeeded }
        .map { it.key }

    println("${deletedUnits.size} юнитов из ${unitsWithFreq.size} будут заменены на медианный профиль")

    return unitsEntries
        .filterNot { it.name in deletedUnits }
        .groupBy { it.name to it.locatePosition }
        .mapValues { (_, entries) ->
            listOf(
                //entries.mean { it.evasiveness.toFloat() },
                entries.mean { it.aggression.toFloat() }, // агрессия вроде влияет на доход, оставлю пока её
                //entries.mean { it.responseAggression.toFloat() },
                //entries.mean { it.shield.toFloat() },
                //entries.mean { it.sourceGoldCount.toFloat() },
                entries.mean { it.goldProfit.toFloat() },
                entries.mean { if (it.goldProfit > 0) 1f else 0f } // винрейт
            ).toFloatArray()
        }
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
            repeat(rounds.first().size - 1) {
                writer.write("p${it},")
            }
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
