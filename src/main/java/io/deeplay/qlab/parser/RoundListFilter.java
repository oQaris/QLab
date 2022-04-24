package io.deeplay.qlab.parser;


import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.history.Round;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class RoundListFilter {
    private static final double EPS = 1E-9;
    
    private static final Predicate<Round> nonZeroSizedLocations = round -> round.getMaxPositionsQuantity() != 0;
    private static final Predicate<Round> unitQtyNotMoreThanMaxPositionsQty = round ->
            round.getOurUnits().size() + round.getOpponentUnits().size() <= round.getMaxPositionsQuantity();
    private static final Predicate<Round> unitsAreOnExistingLocations = round ->
            getAllUnits(round).stream().allMatch(unit ->
                    unit.getLocatePosition() >= 0 || unit.getLocatePosition() < round.getMaxPositionsQuantity());
    private static final Predicate<Round> zeroGoldProfitSum = round -> getAllUnits(round).stream()
            .map(Unit::getGoldProfit).reduce(0., Double::sum) < EPS;
    
    
    private static List<Unit> getAllUnits(Round round) {
        List<Unit> units = new ArrayList<>(round.getOurUnits());
        units.addAll(round.getOpponentUnits());
        
        return units;
    }
    
    
    public static List<Round> filter(List<Round> rounds) {
        return rounds.stream()
                .filter(nonZeroSizedLocations)
                .filter(unitQtyNotMoreThanMaxPositionsQty)
                .filter(unitsAreOnExistingLocations)
                .filter(zeroGoldProfitSum)
                .collect(Collectors.toList());
    }
}
