package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.List;
import java.util.Random;

public class Calculator {

    public static UnitWithLocation calculate(Unit unit, List<EnemyLocation> locations) {
        Random rand = new Random();
        EnemyLocation location = locations.get(rand.nextInt(locations.size()));
        int locatePosition = rand.nextInt(location.getMaxPositionsQuantity());

        return new UnitWithLocation(unit.getName(), unit.getSourceGoldCount(), locatePosition, location.getLocationName());
    }
}
