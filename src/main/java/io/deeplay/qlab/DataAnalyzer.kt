package io.deeplay.qlab

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.models.history.Round
import java.io.File

val data: List<Round> = Parser.parseListRounds(File("testData/anonymized_data.json"))

fun unitsProfiles() {
    val unitStat = data.flatMap { it.ourUnits + it.opponentUnits }.groupBy { it.name }
    /*val unitRounds = buildMap<Unit, List<Round>> {
        data.forEach { round ->
            (round.ourUnits + round.opponentUnits).forEach { unit ->
                compute(unit) { _, list ->
                    if (list == null) listOf()
                    else list + round
                }
            }
        }
    }*/
    val sep = ";"

    println("name${sep}appearances${sep}sourceGoldCount${sep}goldProfit${sep}evasiveness${sep}aggression${sep}responseAggression${sep}shield${sep}")
    unitStat.entries.sortedBy { -it.value.sumOf { it.goldProfit } / it.value.size }.forEach { (name, stats) ->
        print("$name$sep")
        print("${stats.size}$sep")
        print("${stats.sumOf { it.sourceGoldCount }}$sep")
        print("${stats.sumOf { it.goldProfit }}$sep")
        print("${stats.sumOf { it.evasiveness }}$sep")
        print("${stats.sumOf { it.aggression }}$sep")
        print("${stats.sumOf { it.responseAggression }}$sep")
        print("${stats.sumOf { it.shield }}$sep")
        println()
    }
}