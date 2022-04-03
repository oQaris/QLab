package io.deeplay.qlab.parser.models;

import java.util.List;
import java.util.Objects;

public class World {
    private final String worldName;
    private final List<Round> locations;
    private final List<Unit> ourUnits;

    public World() {
        this.worldName = null;
        this.locations = null;
        this.ourUnits = null;
    }

    public World(String worldName, List<Round> locations, List<Unit> ourUnits) {
        this.worldName = worldName;
        this.locations = locations;
        this.ourUnits = ourUnits;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<Round> getLocations() {
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
                && Objects.equals(locations, world.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, locations);
    }
}
