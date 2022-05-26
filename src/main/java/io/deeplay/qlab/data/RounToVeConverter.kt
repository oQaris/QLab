package io.deeplay.qlab.data

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import io.deeplay.qlab.parser.models.history.Round
import java.io.File
import java.util.*

fun main() {
    val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        .run { RoundListFilter.filter(this) }

    /*val zeros = history.count { it.ourUnits.sumOf { it.goldProfit } == 0.0 }
    println("${100 * zeros / history.size} %  нулевых профитов")*/

    val profiles = genProfiles(history)
    //saveProfiles(profiles)
    /*val roundsNormalize = genRoundsNorma(history, profiles)
    saveRounds(roundsNormalize)*/

    val rnds1vs1 = history.filter { it.ourUnits.size == 1 && it.opponentUnits.size == 1 }
    val normalRounds = genRoundsNorma(rnds1vs1, profiles)

    saveRounds(normalRounds, "testData/rnds1vs1.csv")
}

private fun genRoundsNorma(history: List<Round>, profiles: Map<String, FloatArray>): List<FloatArray> {
    val maxPosition = history.maxOf { it.ourUnits.size + it.ourUnits.size }
    val sizeProfile = profiles.entries.first().value.size

    //val locations = history.map { it.locationName }.toSet().sorted().toList()
    val levels = history.map { it.locationLevel }.toSet().sorted().toList()

    val allUnits = history.flatMap { it.ourUnits + it.opponentUnits }
    val maxSrcGold = allUnits.maxOf { it.sourceGoldCount }
    val minSrcGold = allUnits.minOf { it.sourceGoldCount }

    val minGoldProfit = history.minOf { round -> round.ourUnits.sumOf { it.goldProfit } }
    val maxPostGoldProfit = history.maxOf { round -> round.ourUnits.sumOf { it.goldProfit - minGoldProfit } }

    val roundsNormalize = mutableListOf<FloatArray>()
    history.forEach { round ->
        val curRoundNorma = mutableListOf<Float>()
        // Данные о локации
        //curRoundNorma.add(locations.indexOf(round.locationName).toFloat())
        //curRoundNorma.add(levels.indexOf(round.locationLevel).toFloat())
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
                                .plus(((unit.sourceGoldCount - minSrcGold) / maxSrcGold).toFloat())
                                .plus( // 01 - наш, 10 - не наш
                                    if (unit in round.ourUnits) listOf(0f, 1f)
                                    else listOf(1f, 0f)
                                )
                        )
                    } else add(FloatArray(sizeProfile + 3).toList())
                }
            }
        }.flatten()
        curRoundNorma.addAll(roundProfiles)
        // Голд профит
        curRoundNorma.add(((round.ourUnits.sumOf { it.goldProfit } - minGoldProfit) / maxPostGoldProfit).toFloat())
        roundsNormalize.add(curRoundNorma.toFloatArray())
    }
    return roundsNormalize
}

private fun genProfiles(history: List<Round>): Map<String, FloatArray> {
    val means = history.flatMap { it.ourUnits + it.opponentUnits }
        .groupBy { it.name }
        .mapValues { (_, units) ->
            listOf(
                units.mean { it.evasiveness.toDouble() },
                units.mean { it.aggression.toDouble() },
                units.mean { it.responseAggression.toDouble() },
                units.mean { it.shield.toDouble() },
                units.mean { it.sourceGoldCount },
                units.mean { it.goldProfit },
                units.mean { if (it.goldProfit > 0) 1.0 else 0.0 }
            ).map { it.toFloat() }
                .toFloatArray()
        }
    // Нормируем
    val size = means.values.first().size
    val mins = FloatArray(size) { idx ->
        means.values.minOf { it[idx] }
    }
    val means2 = means.mapValues { (_, mean) ->
        mean.mapIndexed { idx, value ->
            value - mins[idx]
        }.toFloatArray()
    }
    val maxs = FloatArray(size) { idx ->
        means2.values.maxOf { it[idx] }
    }
    return means2.mapValues { (_, mean) ->
        mean.mapIndexed { idx, value ->
            value / maxs[idx]
        }.toFloatArray()
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
        writer.write(
            /*"loc,lvl," +
                    "p1_e,p1_a,p1_r,p1_s,p1_gc,p1_gp,gc1," +
                    "p2_e,p2_a,p2_r,p2_s,p2_gc,p2_gp,gc2," +
                    "p3_e,p3_a,p3_r,p3_s,p3_gc,p3_gp,gc3," +
                    "p4_e,p4_a,p4_r,p4_s,p4_gc,p4_gp,gc4," +
                    "p5_e,p5_a,p5_r,p5_s,p5_gc,p5_gp,gc5," +
                    "p6_e,p6_a,p6_r,p6_s,p6_gc,p6_gp,gc6," +
                    "p7_e,p7_a,p7_r,p7_s,p7_gc,p7_gp,gc7," +
                    "p8_e,p8_a,p8_r,p8_s,p8_gc,p8_gp,gc8," +
                    "p9_e,p9_a,p9_r,p9_s,p9_gc,p9_gp,gc9," +
                    "our_gp"*/
            "lvl1,lvl2,lvl3,lvl4,lvl5,lvl6,lvl7,lvl8,lvl9,lvl10," +
                    "p1_e,p1_a,p1_r,p1_s,p1_gc,p1_gp,gc1,vr1,p1_opp,p1_our," +
                    "p2_e,p2_a,p2_r,p2_s,p2_gc,p2_gp,gc2,vr1,p2_opp,p2_our,our_gp"
        )
        writer.newLine()
        rounds.forEach { row ->
            writer.write(row.joinToString(",") { String.format(Locale.ENGLISH, "%.6f", it); })
            writer.newLine()
        }
        writer.flush()
    }
}

private inline fun <T> Collection<T>.mean(selector: (T) -> Double) =
    this.sumOf(selector) / this.size
