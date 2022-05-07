package io.deeplay.qlab.algorithm.eval;

import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.Set;

public class ZeroEvaluator implements IEvaluator {

    @Override
    public double evaluateGoldProfit(Set<UnitWithLocation> units) {
        return 0;
    }
}
