package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.history.Round;

import java.util.List;
import java.util.Random;

public class Calculator {

    public static void calculate(Unit unit, List<Round> rounds) {
        Random rand = new Random();
        unit.setAggression(rand.nextInt(100));
        unit.setEvasiveness(rand.nextInt(100));
        unit.setResponseAggression(rand.nextInt(100));
        unit.setShield(rand.nextInt(100));
    }

}
