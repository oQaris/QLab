package io.deeplay.qlab.parser.models.input;

import io.deeplay.qlab.parser.models.UnitWithResult;

import java.util.List;
import java.util.Objects;

public class World {
    private final String worldName;
    private final List<EnemyLocation> locations;
    private final List<UnitWithResult> ourUnits;

    public World(String worldName, List<EnemyLocation> locations, List<UnitWithResult> ourUnitWithResults) {
        this.worldName = worldName;
        this.locations = locations;
        this.ourUnits = ourUnitWithResults;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<EnemyLocation> getLocations() {
        return locations;
    }

    public List<UnitWithResult> getOurUnits() {
        return ourUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof World world)) return false;
        return Objects.equals(worldName, world.worldName)
                && Objects.equals(locations, world.locations)
                && Objects.equals(ourUnits, world.ourUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, locations, ourUnits);
    }
}
