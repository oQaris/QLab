package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.Set;

public class MegaFinder {
    private final IEvaluator strategy;

    public MegaFinder(IEvaluator strategy) {
        this.strategy = strategy;
    }

    public Set<UnitWithLocation> findDisposition(Set<Unit> units, Set<EnemyLocation> locations) {
        return PlacerKt.findBestArrangement(units, locations, strategy);
    }
}
