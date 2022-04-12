package io.deeplay.qlab.parser.models.output;

import java.util.Objects;

public class UnitWithLocation {
    private final String name;
    private final double sourceGoldCount;
    private final int locatePosition;
    private final String locationName;

    public UnitWithLocation(String name, double sourceGoldCount, int locatePosition, String locationName) {
        this.name = name;
        this.sourceGoldCount = sourceGoldCount;
        this.locatePosition = locatePosition;
        this.locationName = locationName;
    }

    public String getName() {
        return name;
    }

    public double getSourceGoldCount() {
        return sourceGoldCount;
    }

    public int getLocatePosition() {
        return locatePosition;
    }

    public String getLocationName() {
        return locationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitWithLocation that)) return false;
        return Double.compare(that.sourceGoldCount, sourceGoldCount) == 0
                && locatePosition == that.locatePosition
                && Objects.equals(name, that.name)
                && Objects.equals(locationName, that.locationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sourceGoldCount, locatePosition, locationName);
    }
}
