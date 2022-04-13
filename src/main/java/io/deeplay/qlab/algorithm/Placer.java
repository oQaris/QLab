package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.Set;


public class Placer {

    public UnitWithLocation findDisposition(Set<Unit> ourUnits, Set<EnemyLocation> locations) {
        // TODO PowerSet
        
//        Evaluator eval = new Evaluator();
//        int maxGoldProfit = 0;
//        UnitWithLocation out = new UnitWithLocation(unit.getName());
//
//        for (EnemyLocation location : locations) {
//            for (int position = 0; position < location.getMaxPositionsQuantity(); position++) {
//
//                int goldProfit = eval.estimateGoldProfit(unit, location, position);
//                if (goldProfit > maxGoldProfit) {
//                    maxGoldProfit = goldProfit;
//                    out.setSourceGoldCount(goldProfit);
//                    out.setLocationName(location.getLocationName());
//                    out.setLocatePosition(position);
//                }
//            }
//        }
//        return out;
        
        return null;
    }
}
