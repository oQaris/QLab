package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.Set;

public interface IEvaluator {

    double evaluateGoldProfit(Set<UnitWithLocation> units);
}
