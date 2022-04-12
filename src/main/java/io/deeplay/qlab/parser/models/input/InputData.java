package io.deeplay.qlab.parser.models.input;

import io.deeplay.qlab.parser.models.Unit;

import java.util.List;
import java.util.Objects;

public class InputData {
    private final String worldName;
    private final List<EnemyLocation> locations;
    private final List<Unit> ourUnits;

    public InputData(String worldName, List<EnemyLocation> locations, List<Unit> ourUnits) {
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
        if (!(o instanceof InputData inputData)) return false;
        return Objects.equals(worldName, inputData.worldName)
                && Objects.equals(locations, inputData.locations)
                && Objects.equals(ourUnits, inputData.ourUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, locations, ourUnits);
    }
}
