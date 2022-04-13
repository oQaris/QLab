package io.deeplay.qlab.parser.models.input;

import io.deeplay.qlab.parser.models.Unit;

import java.util.List;
import java.util.Objects;

public class World {
    private final String worldName;
    private final List<EnemyLocation> locations;
    private final List<Unit> ourUnits;

    public World(String worldName, List<EnemyLocation> locations, List<Unit> ourUnits) {
        this.worldName = worldName;
        this.locations = locations;
        this.ourUnits = ourUnits;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<EnemyLocation> getLocations() {
        return locations;
    }

    public List<Unit> getOurUnits() {
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
