package io.deeplay.qlab.data

class Normalizer(val context: NormContext) {

    companion object {

        fun fitMinimax(history: List<FloatArray>, from: Int = 0, to: Int = 1) = Normalizer(
            context = buildList {
                history.forEachColumn { column ->
                    val (max, min) = column.maxOf { it } to column.minOf { it }
                    val mmba = (max - min) / (to - from)
                    add(min - from * mmba to mmba)
                }
            }.toContext()
        )

        fun fitZNorm(history: List<FloatArray>) = Normalizer(
            context = buildList {
                history.forEachColumn { column ->
                    val (max, min) = column.maxOf { it } to column.minOf { it }
                    val contextCol = // не трогаем вероятности и one hot
                        if (max <= 1 && min >= -1) 0f to 1f
                        else column.contextForNormaZ()
                    add(contextCol)
                }
            }.toContext()
        )

        private fun Collection<Float>.contextForNormaZ(): Pair<Float, Float> {
            val mean = this.mean { it }
            val sd = this.standardDeviation { it }
            return mean to sd
        }

        private fun Collection<FloatArray>.forEachColumn(action: (List<Float>) -> Unit) {
            for (nCol in this.first().indices) {
                val column = this.map { it[nCol] }
                action(column)
            }
        }

        private fun Collection<Pair<Float, Float>>.toContext() = this.unzip().let {
            NormContext(it.first.toFloatArray(), it.second.toFloatArray())
        }

        class NormContext(val subtrahend: FloatArray, val divider: FloatArray)
    }

    fun transform(data: List<FloatArray>, vararg excludingCols: Int): List<FloatArray> {
        return data.map {
            it.applyNormByRow(context, excludingCols).toFloatArray()
        }
    }

    private fun FloatArray.applyNormByRow(context: NormContext, excludingCols: IntArray): List<Float> {
        return this.zip(context.subtrahend.zip(context.divider))
            .mapIndexed { idx, triple ->
                if (idx !in excludingCols)
                    (triple.first - triple.second.first) / triple.second.second
                else triple.first
            }
    }
}
