package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.List;

public class MegaFinder {

    public UnitWithLocation findDisposition(Unit unit, List<EnemyLocation> locations) {
        Evaluator eval = new Evaluator();
        double maxGoldProfit = 0.0;
        UnitWithLocation out = new UnitWithLocation(unit.getName());

        for (EnemyLocation location : locations) {
            for (int position = 0; position < location.getMaxPositionsQuantity(); position++) {

                double goldProfit = eval.estimatedGoldProfit(unit, location, position);
                if (goldProfit > maxGoldProfit) {
                    maxGoldProfit = goldProfit;
                    out.setSourceGoldCount(goldProfit);
                    out.setLocationName(location.getLocationName());
                    out.setLocatePosition(position);
                }
            }
        }
        return out;
    }
}
