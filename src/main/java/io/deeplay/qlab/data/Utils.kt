package io.deeplay.qlab.data

import krangl.mean
import kotlin.math.pow
import kotlin.math.sqrt

fun <T> splitData(data: List<T>, testFraq: Double, shuffle: Boolean = false): Pair<List<T>, List<T>> {
    require(testFraq in 0.0..1.0) { "Некорректный testFraq" }

    val dataCopy = if (shuffle) data.shuffled() else data

    val testSize = (dataCopy.size * testFraq).toInt()
    val trainData = dataCopy.take(data.size - testSize)
    val testData = dataCopy.takeLast(testSize)

    return trainData to testData
}

inline fun <T> Collection<T>.mean(selector: (T) -> Float) =
    this.sumOf { selector.invoke(it).toDouble() / this.size }.toFloat()

inline fun <T> Collection<T>.median(selector: (T) -> Float): Float {
    if (this.isEmpty()) return 0f
    val sorted = this.map(selector).sorted()
    val n = this.size
    return if (n % 2 == 1) sorted[(n - 1) / 2]
    else (sorted[n / 2 - 1] + sorted[n / 2]) / 2
}

inline fun <T> Collection<T>.standardDeviation(selector: (T) -> Float): Float {
    val mean = this.mean(selector)
    return sqrt(this.sumOf {
        (selector(it).toDouble() - mean).pow(2) / this.size
    }).toFloat()
}

fun r2Score(y: List<Double>, yPred: List<Double>): Double {
    val rss = y.mapIndexed { idx, gp -> (gp - yPred[idx]).pow(2) }.sum()
    val yMean = y.mean()
    val tss = y.sumOf { (it - yMean).pow(2) }
    return 1 - rss / tss
}
