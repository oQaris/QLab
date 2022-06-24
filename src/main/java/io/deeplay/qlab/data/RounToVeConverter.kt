package io.deeplay.qlab.data

import io.deeplay.qlab.parser.Parser
import io.deeplay.qlab.parser.RoundListFilter
import java.io.File
import java.util.*

fun main() {
    println("Парсинг...")
    val history = Parser.parseRoundList(File("testData/anonymized_data.json"))
        .run { RoundListFilter.filter(this) }
    println("Всего данных: ${history.size}")

    println("Генерация профилей...")
    val standardizer = Standardizer.fit(history)

    println("Медианный профиль юнита: " + standardizer.medianUnitProfile)
    println("Медианный профиль локации: " + standardizer.medianLocationProfile)

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
    var roundsStd = standardizer.transformAll(rounds, trimMaxPos = true)

    val normContext = genMinimaxNormContext(roundsStd)
    println("Контекст нормализации:")
    println(normContext.first.joinToString { String.format(Locale.ENGLISH, "%.8f", it) })
    println(normContext.second.joinToString { String.format(Locale.ENGLISH, "%.8f", it) })

    println("Нормализация данных...")
    roundsStd = normalize(roundsStd) { normContext }

    println("Сохранение в файл...")
    saveRounds(roundsStd, "trainData/11.csv")
    println("Сохранено ${roundsStd.size} ${roundsStd.first().size}-мерных векторов")
}

private fun normalize(
    data: List<FloatArray>,
    normContext: (List<FloatArray>) -> Pair<FloatArray, FloatArray> = ::genZNormContext
): List<FloatArray> {
    return data.map { it.applyNormByRow(normContext(data)).toFloatArray() }
}

private fun saveRounds(rounds: List<FloatArray>, fileName: String) {
    File(fileName)
        .also { it.parentFile.mkdirs() }
        .bufferedWriter().use { writer ->
            repeat(rounds.first().size - 1) {
                writer.write("p${it},")
            }
            // Для 1 на 1 будет такой порядок:
            //writer.write("evl,agl,ral,shl,sgl,gpl,vrl,lvl1,lvl2,lvl3,lvl4,lvl5,lvl6,lvl7,lvl8,lvl9,lvl10,ev1,ag1,ra1,sh1,sg1,gp1,vr1,sgc1,our1,ev2,ag2,ra2,sh2,sg2,gp2,vr2,sgc2,our2,")
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
