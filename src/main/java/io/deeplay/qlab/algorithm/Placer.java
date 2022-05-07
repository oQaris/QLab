package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.algorithm.eval.IEvaluator;
import io.deeplay.qlab.parser.models.UnitWithResult;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.Set;

public class Placer {

    private final IEvaluator strategy;

    public Placer(IEvaluator strategy) {
        this.strategy = strategy;
    }

    public Set<UnitWithLocation> findDisposition(Set<UnitWithResult> unitWithResults, Set<EnemyLocation> locations) {
        return PlacerHelperKt.findBestArrangement(unitWithResults, locations, strategy);
    }
}
