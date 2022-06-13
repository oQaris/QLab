package io.deeplay.qlab.data

import kotlin.math.pow
import kotlin.math.sqrt

inline fun <T> Collection<T>.mean(selector: (T) -> Float) =
    this.sumOf { selector.invoke(it).toDouble() / this.size }.toFloat()

inline fun <T> Collection<T>.standardDeviation(selector: (T) -> Float): Float {
    val mean = this.mean(selector)
    return sqrt(this.sumOf {
        (selector(it).toDouble() - mean).pow(2) / this.size
    }).toFloat()
}

fun FloatArray.applyNormaByRow(context: Pair<FloatArray, FloatArray>): List<Float> {
    return this.zip(context.first.zip(context.second))
        .map { (it.first - it.second.first) / it.second.second }
}


fun minimaxNormaContext(data: List<FloatArray>): Pair<FloatArray, FloatArray> {
    return buildList {
        for (nCol in data.first().indices) {
            val column = data.map { it[nCol] }
            val (max, min) = column.maxOf { it } to column.minOf { it }
            add(min to max - min)
        }
    }.unzip().let {
        it.first.toFloatArray() to it.second.toFloatArray()
    }
}

fun zNormaContext(data: List<FloatArray>): Pair<FloatArray, FloatArray> {
    return buildList {
        for (nCol in data.first().indices) {
            val column = data.map { it[nCol] }
            val (max, min) = column.maxOf { it } to column.minOf { it }
            val contextCol = // не трогаем вероятности и one hot
                if (max <= 1 && min >= -1) emptyNormalizationContext()
                else column.contextForNormaZ()
            add(contextCol)
        }
    }.unzip().let {
        it.first.toFloatArray() to it.second.toFloatArray()
    }
}

fun emptyNormalizationContext() = 0f to 1f

fun Collection<Float>.contextForNormaZ(): Pair<Float, Float> {
    val mean = this.mean { it }
    val sd = this.standardDeviation { it }
    return mean to sd
}
