package io.deeplay.qlab.parser.models.output;

import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;

import java.util.Objects;

public class UnitWithLocation extends Unit {
    private EnemyLocation location;

    public UnitWithLocation(String name, double sourceGoldCount, int locatePosition, EnemyLocation location) {
        super(name, sourceGoldCount, locatePosition);
        this.location = location;
    }

    public EnemyLocation getLocation() {
        return location;
    }

    public void setLocation(EnemyLocation location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitWithLocation that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), location);
    }
}
