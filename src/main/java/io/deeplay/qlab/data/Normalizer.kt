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
                    add(orEmptyNorm(column) {
                        val mean = column.mean { it }
                        val sd = column.standardDeviation { it }
                        mean to sd
                    })
                }
            }.toContext()
        )

        fun fitRobust(history: List<FloatArray>) = Normalizer(
            context = buildList {
                history.forEachColumn { column ->
                    add(orEmptyNorm(column) {
                        val median = column.median { it }
                        val q25 = column.quantile(0.25f) { it }
                        val q75 = column.quantile(0.75f) { it }
                        median to q75 - q25
                    })
                }
            }.toContext()
        )

        private fun orEmptyNorm(column: Collection<Float>, context: () -> Pair<Float, Float>): Pair<Float, Float> {
            val (max, min) = column.maxOf { it } to column.minOf { it }
            return if (max <= 1 && min >= -1) 0f to 1f
            else context()
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

    fun transformAll(data: List<FloatArray>, vararg excludingCols: Int): List<FloatArray> {
        return data.map {
            transform(it, *excludingCols)
        }
    }

    fun transform(data: FloatArray, vararg excludingCols: Int): FloatArray {
        return data.zip(context.subtrahend.zip(context.divider))
            .mapIndexed { idx, triple ->
                if (idx !in excludingCols)
                    (triple.first - triple.second.first) / triple.second.second
                else triple.first
            }.toFloatArray()
    }
}
