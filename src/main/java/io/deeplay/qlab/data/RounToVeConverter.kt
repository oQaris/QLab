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

    println("Генерация профилей...")
    val profiles = genProfiles(history)

    // Можно настроить выбор раундов
    val rounds = history.filter { it.ourUnits.size == 1 && it.opponentUnits.size == 2 }

    println("Стандартизация данных...")
    var roundsStd = standardizeRounds(rounds, profiles)

    val normContext = zNormaContext(roundsStd)
    println("Контекст для нормализации:")
    println(normContext.first.joinToString { String.format(Locale.ENGLISH, "%.6f", it) })
    println(normContext.second.joinToString { String.format(Locale.ENGLISH, "%.6f", it) })

    println("Нормализация данных...")
    roundsStd = normalizeStd(roundsStd) { normContext }

    println("Сохранение в файл...")
    saveRounds(roundsStd, "testData/rnds1vs2z.csv")
    println("Сохранено ${roundsStd.size} ${roundsStd.first().size}-мерных векторов")
}

private fun standardizeRounds(
    history: List<Round>,
    profiles: Map<String, FloatArray>
): List<FloatArray> {

    val sizeDataOneUnit = // длина профиля + дополнительные данные
        profiles.entries.first().value.size + 3

    val levels = history.map { it.locationLevel }.distinct().sorted()
    require(levels.size == 10)
    val maxPosition = history.maxOf { it.ourUnits.size + it.ourUnits.size }

    val roundsNormalize = mutableListOf<FloatArray>()

    history.forEach { round ->
        val curRoundNorma = mutableListOf<Float>()
        // One-Hot encoding
        curRoundNorma.addAll(
            FloatArray(levels.size)
            { if (levels[it] == round.locationLevel) 1f else 0f }.toList()
        )
        // Данные о расположенных юнитах
        val roundProfiles = (round.ourUnits + round.opponentUnits).let { units ->
            buildList {
                repeat(maxPosition) { pos ->
                    val unit = units.firstOrNull { it.locatePosition == pos }
                    if (unit != null) {
                        val rawProfile = profiles[unit.name]!!
                        add(
                            rawProfile.toList()
                                // добавляем в конец sourceGold в раунде
                                .plus((unit.sourceGoldCount).toFloat())
                                .plus( // 01 - наш, 10 - не наш
                                    if (unit in round.ourUnits) listOf(0f, 1f)
                                    else listOf(1f, 0f)
                                ).also { require(it.size == sizeDataOneUnit) }
                        )
                    } else add(FloatArray(sizeDataOneUnit).toList())
                }
            }
        }.flatten()
        curRoundNorma.addAll(roundProfiles)
        // Голд профит
        curRoundNorma.add((round.ourUnits.sumOf { it.goldProfit }).toFloat())

        roundsNormalize.add(curRoundNorma.toFloatArray())
    }
    return roundsNormalize
}

private fun normalizeStd(
    data: List<FloatArray>,
    norma: (List<FloatArray>) -> Pair<FloatArray, FloatArray> = ::zNormaContext
): List<FloatArray> {
    return data.map { it.applyNormaByRow(norma(data)).toFloatArray() }
}

private fun genProfiles(history: List<Round>): Map<String, FloatArray> {
    return history.flatMap { it.ourUnits + it.opponentUnits }
        .groupBy { it.name }
        .mapValues { (_, units) ->
            listOf(
                units.mean { it.evasiveness.toFloat() },
                units.mean { it.aggression.toFloat() },
                units.mean { it.responseAggression.toFloat() },
                units.mean { it.shield.toFloat() },
                units.mean { it.sourceGoldCount.toFloat() },
                units.mean { it.goldProfit.toFloat() },
                units.mean { if (it.goldProfit > 0) 1f else 0f }
            ).toFloatArray()
        }
}

private fun saveProfiles(profiles: Map<String, FloatArray>, fileName: String = "testData/profiles.csv") {
    File(fileName).bufferedWriter().use { writer ->
        writer.write("name,p_e,p_a,p_r,p_s,p_gc,p_gp,gc")
        writer.newLine()
        profiles.entries.forEach { (name, row) ->
            writer.write("$name,")
            writer.write(row.joinToString(","))
            writer.newLine()
        }
        writer.flush()
    }
}

private fun saveRounds(rounds: List<FloatArray>, fileName: String = "testData/rounds_normalize.csv") {
    File(fileName).bufferedWriter().use { writer ->
        val spaceForUnits = rounds.first().size - 11
        require(spaceForUnits % 10 == 0)
        writer.write("lvl1,lvl2,lvl3,lvl4,lvl5,lvl6,lvl7,lvl8,lvl9,lvl10,")
        repeat(spaceForUnits / 10) {
            writer.write("p${it}_e,p${it}_a,p${it}_r,p${it}_s,p${it}_gc,p${it}_gp,vr${it},gc${it},p${it}_opp,p${it}_our,")
        }
        writer.write("our_gp")
        writer.newLine()
        rounds.forEach { row ->
            writer.write(row.joinToString(",") { String.format(Locale.ENGLISH, "%.8f", it); })
            writer.newLine()
        }
        writer.flush()
    }
}
