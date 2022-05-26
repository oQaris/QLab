package io.deeplay.qlab.data

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import io.deeplay.qlab.parser.models.history.Round
import java.io.File

fun main() {
    val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        .run { RoundListFilter.filter(this) }

    val lvls = history.map { it.locationLevel }.toSet().sorted()
    val locs = history.groupingBy { it.locationLevel }
        .eachCount().entries
        .sortedBy { it.value }
    println(locs.joinToString("\n"))
}

fun unitsProfiles() {
    val data: List<Round> =
        Parser.parseRoundList(File("C:\\Users\\oQaris\\Downloads\\Telegram Desktop\\filtered1to1.json"))
    val unitStat = data.flatMap { it.ourUnits + it.opponentUnits }
        .groupBy { it.name }

    val sep = ";"
    println("name${sep}appearances${sep}sourceGoldCount${sep}goldProfit${sep}evasiveness${sep}aggression${sep}responseAggression${sep}shield${sep}")

    unitStat.entries.sortedBy { nameToUnit ->
        -nameToUnit.value.sumOf { it.goldProfit } / nameToUnit.value.size
    }.forEach { (name, stats) ->
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
