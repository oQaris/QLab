package io.deeplay.qlab.algorithm;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.World;

import java.util.List;

public class MegaFinder {

    public List<Unit> findOptDisposition(World world) {
        return world.getOurUnits();
    }

}
