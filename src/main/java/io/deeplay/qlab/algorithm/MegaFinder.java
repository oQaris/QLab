package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.InputData;

import java.util.List;

public class MegaFinder {

    public List<Unit> findOptDisposition(InputData world) {
        return world.getOurUnits();
    }

}
