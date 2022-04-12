package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;

import java.util.Random;

public class Evaluator {

    public double estimateGoldProfit(Unit unit, EnemyLocation location, int locatePosition) {
        return new Random().nextDouble(unit.getGoldProfit());
    }
}
