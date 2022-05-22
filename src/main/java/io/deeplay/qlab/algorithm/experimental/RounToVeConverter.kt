package io.deeplay.qlab.algorithm.experimental

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import io.deeplay.qlab.parser.models.history.Round
import java.io.File

const val MAX_POSITION = 9

fun main() {
    val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        .run { RoundListFilter.filter(this) }

    val profiles = genProfiles(history)
    //saveProfiles(profiles)
    val sizeProfile = profiles.entries.first().value.size

    val locations = history.map { it.locationName }.toSet().toList()
    val levels = history.map { it.locationLevel }.toSet().toList()

    val roundsNormalize = mutableListOf<FloatArray>()
    history.forEach { round ->
        val curRoundNorma = mutableListOf<Float>()
        // Данные о локации
        curRoundNorma.add(locations.indexOf(round.locationName).toFloat())
        curRoundNorma.add(levels.indexOf(round.locationLevel).toFloat())
        // Данные о расположенных юнитах
        val roundProfiles = (round.ourUnits + round.opponentUnits).let { units ->
            buildList {
                repeat(MAX_POSITION) { pos ->
                    val unit = units.firstOrNull { it.locatePosition == pos }
                    if (unit != null) {
                        val rawProfile = profiles[unit.name]!!
                        add(
                            (if (unit in round.ourUnits) rawProfile.toList()
                            else rawProfile.map { -it }) // делаем отрицательным для противников
                                .plus(unit.sourceGoldCount.toFloat()) // добавляем в конец sourceGold в раунде
                        )
                    } else add(FloatArray(sizeProfile + 1).toList())
                }
            }
        }.flatten()
        curRoundNorma.addAll(roundProfiles)
        // Голд профит
        curRoundNorma.add(round.ourUnits.sumOf { it.goldProfit }.toFloat())
        roundsNormalize.add(curRoundNorma.toFloatArray())
    }
    saveRounds(roundsNormalize)
}

private fun genProfiles(history: List<Round>): Map<String, FloatArray> {
    val means = history.flatMap { it.ourUnits + it.opponentUnits }
        .groupBy { it.name }
        .mapValues { (_, units) ->
            listOf(
                units.mean { it.evasiveness.toDouble() }.toFloat(),
                units.mean { it.aggression.toDouble() }.toFloat(),
                units.mean { it.responseAggression.toDouble() }.toFloat(),
                units.mean { it.shield.toDouble() }.toFloat(),
                units.mean { it.sourceGoldCount }.toFloat(),
                units.mean { it.goldProfit }.toFloat()
            ).toFloatArray()
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

private fun saveProfiles(profiles: Map<String, FloatArray>) {
    File("testData/profiles.csv").bufferedWriter().use { writer ->
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

private fun saveRounds(rounds: MutableList<FloatArray>) {
    File("testData/rounds_normalize.csv").bufferedWriter().use { writer ->
        writer.write(
            "loc,lvl," +
                    "p1_e,p1_a,p1_r,p1_s,p1_gc,p1_gp,gc1," +
                    "p2_e,p2_a,p2_r,p2_s,p2_gc,p2_gp,gc2," +
                    "p3_e,p3_a,p3_r,p3_s,p3_gc,p3_gp,gc3," +
                    "p4_e,p4_a,p4_r,p4_s,p4_gc,p4_gp,gc4," +
                    "p5_e,p5_a,p5_r,p5_s,p5_gc,p5_gp,gc5," +
                    "p6_e,p6_a,p6_r,p6_s,p6_gc,p6_gp,gc6," +
                    "p7_e,p7_a,p7_r,p7_s,p7_gc,p7_gp,gc7," +
                    "p8_e,p8_a,p8_r,p8_s,p8_gc,p8_gp,gc8," +
                    "p9_e,p9_a,p9_r,p9_s,p9_gc,p9_gp,gc9," +
                    "our_gp"
        )
        writer.newLine()
        rounds.forEach { row ->
            writer.write(row.joinToString(","))
            writer.newLine()
        }
        writer.flush()
    }
}

private inline fun <T> Collection<T>.mean(selector: (T) -> Double) =
    this.sumOf(selector) / this.size
