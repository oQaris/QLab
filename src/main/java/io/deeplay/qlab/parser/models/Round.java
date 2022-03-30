package io.deeplay.qlab.parser.models;

import java.util.List;
import java.util.Objects;

public class Round {
    private final String roundId;
    private final String locationName;
    private final int locationLevel;
    private final int maxPositionsQuantity;
    private final List<Unit> opponentUnits;
    private final List<Unit> ourUnits;

    public Round() {
        this.roundId = null;
        this.locationName = null;
        this.locationLevel = 0;
        this.maxPositionsQuantity = 0;
        this.opponentUnits = null;
        this.ourUnits = null;
    }

    public Round(String roundId, String locationName, int locationLevel, int maxPositionsQuantity, List<Unit> opponentUnits, List<Unit> ourUnits) {
        this.roundId = roundId;
        this.locationName = locationName;
        this.locationLevel = locationLevel;
        this.maxPositionsQuantity = maxPositionsQuantity;
        this.opponentUnits = opponentUnits;
        this.ourUnits = ourUnits;
    }

    public String getRoundId() {
        return roundId;
    }

    public String getLocationName() {
        return locationName;
    }

    public int getLocationLevel() {
        return locationLevel;
    }

    public int getMaxPositionsQuantity() {
        return maxPositionsQuantity;
    }

    public List<Unit> getOpponentUnits() {
        return opponentUnits;
    }

    public List<Unit> getOurUnits() {
        return ourUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Round round)) return false;
        return locationLevel == round.locationLevel
                && maxPositionsQuantity == round.maxPositionsQuantity
                && Objects.equals(roundId, round.roundId)
                && Objects.equals(locationName, round.locationName)
                && Objects.equals(opponentUnits, round.opponentUnits)
                && Objects.equals(ourUnits, round.ourUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundId, locationName, locationLevel, maxPositionsQuantity, opponentUnits, ourUnits);
    }
}
