package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.algorithm.eval.IEvaluator;
import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.Set;

public class Placer {

    private final IEvaluator strategy;

    public Placer(IEvaluator strategy) {
        this.strategy = strategy;
    }

    public Set<UnitWithLocation> findDisposition(Set<Unit> units, Set<EnemyLocation> locations) {
        return PlacerHelperKt.findBestArrangement(units, locations, strategy);
    }
}
