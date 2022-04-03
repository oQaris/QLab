package io.deeplay.qlab.parser.models;

import java.util.List;
import java.util.Objects;

public class Location {
    private final String name;
    private final String roundId;
    private final int maxPositionQuantity;
    private final List<Unit> opponentUnits;

    public Location(String name, String roundId, int maxPositionQuantity, List<Unit> opponentUnits) {
        this.name = name;
        this.roundId = roundId;
        this.maxPositionQuantity = maxPositionQuantity;
        this.opponentUnits = opponentUnits;
    }

    public String getName() {
        return name;
    }

    public String getRoundId() {
        return roundId;
    }

    public int getMaxPositionQuantity() {
        return maxPositionQuantity;
    }

    public List<Unit> getOpponentUnits() {
        return opponentUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;
        return maxPositionQuantity == location.maxPositionQuantity
                && Objects.equals(name, location.name)
                && Objects.equals(roundId, location.roundId)
                && Objects.equals(opponentUnits, location.opponentUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roundId, maxPositionQuantity, opponentUnits);
    }
}
