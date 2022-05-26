package io.deeplay.qlab.algorithm.eval.ml

import io.deeplay.qlab.algorithm.eval.IEvaluator
import io.deeplay.qlab.parser.models.output.UnitWithLocation

class AIEvaluator : IEvaluator {
    private val predictor = OnnxPredictor("ml/torchQlab.onnx")

    override fun evaluateGoldProfit(units: MutableSet<UnitWithLocation>): Double {
        return units.groupBy { it.location }
            .entries.sumOf { (_, units) ->
                predictor.predict(units.toFloatArray()).single().toDouble()
            }
    }

    private fun List<UnitWithLocation>.toFloatArray(): FloatArray {
        TODO("Добавить нормирование как для раунда")
    }
}
